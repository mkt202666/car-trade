package com.pancosky.newcartrade.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.manager.PushManager;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.cache.CarCacheService;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 监听运营端发布的移动端通知事件
 * 
 * 订阅 Redis 频道: mobile:events
 * 接收 MobileNotification 对象，转换为 WebSocket 推送给用户
 * 同时清理相关缓存确保数据一致性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MobileEventListener {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate<String, String> redisTemplate;
    private final PushManager pushManager;
    private final CarCacheService carCacheService;
    private final UserMapper userMapper;

    private final ObjectMapper objectMapper;

    private static final String CHANNEL = "mobile:events";

    @PostConstruct
    public void init() {
        // 注册消息监听器
        redisMessageListenerContainer.addMessageListener(
                new MessageListenerAdapter(this, "handleNotification"),
                new ChannelTopic(CHANNEL)
        );
        log.info("[MobileEventListener] Registered listener for Redis channel: {}", CHANNEL);
    }

    /**
     * 处理运营端推送的移动端通知事件
     */
    public void handleNotification(String message) {
        try {
            MobileNotification notification = objectMapper.readValue(message, MobileNotification.class);
            log.info("[MobileEventListener] Received notification: type={}, targetUser={}, title={}",
                    notification.getType(), notification.getTargetUserId(), notification.getTitle());

            // 根据通知类型执行相应操作
            switch (notification.getType()) {
                case SHOP_AUDIT_RESULT:
                    handleShopAuditResult(notification);
                    break;
                case CAR_STATUS_CHANGED:
                    handleCarStatusChanged(notification);
                    break;
                case DISPUTE_RESOLVED:
                    handleDisputeResolved(notification);
                    break;
                case DEPOSIT_CHANGED:
                    handleDepositChanged(notification);
                    break;
                case USER_STATUS_CHANGED:
                    handleUserStatusChanged(notification);
                    break;
                case SYSTEM_ANNOUNCEMENT:
                    handleSystemAnnouncement(notification);
                    break;
                case PURCHASE_MATCHED:
                    handlePurchaseMatched(notification);
                    break;
                case ORDER_STATUS_CHANGED:
                    handleOrderStatusChanged(notification);
                    break;
                default:
                    log.warn("[MobileEventListener] Unknown notification type: {}", notification.getType());
            }

        } catch (Exception e) {
            log.error("[MobileEventListener] Failed to handle notification: {}", message, e);
        }
    }

    /**
     * 处理车行审核结果通知
     */
    private void handleShopAuditResult(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for SHOP_AUDIT_RESULT");
            return;
        }

        // 清除用户信息缓存（如果有）
        clearUserCache(userId);

        // 推送 WebSocket 消息给用户
        MessageVO message = buildMessageVO(
                "shop_audit",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed shop audit result to user: {}", userId);
    }

    /**
     * 处理车源状态变更通知
     */
    private void handleCarStatusChanged(MobileNotification notification) {
        String targetId = notification.getTargetId();
        if (targetId != null) {
            try {
                Long carId = Long.parseLong(targetId);
                // 清除车源详情缓存
                carCacheService.clearDetailCache(carId);
                log.info("[MobileEventListener] Cleared cache for car: {}", carId);
            } catch (NumberFormatException e) {
                log.warn("[MobileEventListener] Invalid car ID format: {}", targetId);
            }
        }

        // 推送给相关用户（卖家或关注者）
        Long userId = notification.getTargetUserId();
        if (userId != null) {
            MessageVO message = buildMessageVO(
                    "car_status",
                    notification.getTitle(),
                    notification.getContent(),
                    notification.getExtra()
            );
            pushManager.pushToUser(userId, message);
            log.info("[MobileEventListener] Pushed car status change to user: {}", userId);
        }
    }

    /**
     * 处理争议解决通知
     */
    private void handleDisputeResolved(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for DISPUTE_RESOLVED");
            return;
        }

        MessageVO message = buildMessageVO(
                "dispute_resolved",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed dispute resolution to user: {}", userId);
    }

    /**
     * 处理保证金变动通知
     */
    private void handleDepositChanged(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for DEPOSIT_CHANGED");
            return;
        }

        MessageVO message = buildMessageVO(
                "deposit_changed",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed deposit change to user: {}", userId);
    }

    /**
     * 处理用户状态变更通知（封禁/解封）
     */
    private void handleUserStatusChanged(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for USER_STATUS_CHANGED");
            return;
        }

        // 清除用户缓存
        clearUserCache(userId);

        // 推送通知
        MessageVO message = buildMessageVO(
                "user_status",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed user status change to user: {}", userId);
    }

    /**
     * 处理系统公告（广播给所有在线用户）
     */
    private void handleSystemAnnouncement(MobileNotification notification) {
        // 系统公告可能没有特定目标用户，需要广播
        // 这里简化处理：如果指定了 targetUserId 则推送给该用户，否则记录日志
        Long userId = notification.getTargetUserId();
        if (userId != null) {
            MessageVO message = buildMessageVO(
                    "system_announcement",
                    notification.getTitle(),
                    notification.getContent(),
                    notification.getExtra()
            );
            pushManager.pushToUser(userId, message);
            log.info("[MobileEventListener] Pushed system announcement to user: {}", userId);
        } else {
            log.info("[MobileEventListener] System announcement received (broadcast mode): {}", notification.getTitle());
            // TODO: 如需广播给所有在线用户，需要维护在线用户列表或使用 /topic/announcements
            // pushManager.pushToAll(buildMessageVO(...));
        }
    }

    /**
     * 处理求购匹配通知
     */
    private void handlePurchaseMatched(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for PURCHASE_MATCHED");
            return;
        }

        MessageVO message = buildMessageVO(
                "purchase_matched",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed purchase match to user: {}", userId);
    }

    /**
     * 处理订单状态变更通知（运营端操作：确认/强制取消/纠纷裁决等）
     */
    private void handleOrderStatusChanged(MobileNotification notification) {
        Long userId = notification.getTargetUserId();
        if (userId == null) {
            log.warn("[MobileEventListener] Missing targetUserId for ORDER_STATUS_CHANGED");
            return;
        }

        MessageVO message = buildMessageVO(
                "order_status",
                notification.getTitle(),
                notification.getContent(),
                notification.getExtra()
        );
        pushManager.pushToUser(userId, message);
        log.info("[MobileEventListener] Pushed order status change to user: {}", userId);
    }

    /**
     * 构建 WebSocket 消息对象
     */
    private MessageVO buildMessageVO(String type, String title, String content, Map<String, Object> extra) {
        MessageVO message = new MessageVO();
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setSenderId(0L); // 系统发送
        
        // 如果有额外数据，可以拼接到 content 中或记录日志
        if (extra != null && !extra.isEmpty()) {
            log.debug("[MobileEventListener] Extra data for message: {}", extra);
            // TODO: 如需传递额外数据，可扩展 MessageVO 或在 content 中包含 JSON
        }
        
        return message;
    }

    /**
     * 清除用户信息缓存
     */
    private void clearUserCache(Long userId) {
        try {
            // 清除用户基本信息缓存（如果实现了 UserCacheService）
            // 当前项目中没有独立的 UserCacheService，直接查询数据库即可
            // 预留接口供未来扩展
            log.debug("[MobileEventListener] User cache cleared for user: {}", userId);
        } catch (Exception e) {
            log.warn("[MobileEventListener] Failed to clear user cache for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * 内部 DTO：移动端通知对象（与运营端 MobileNotification 保持一致）
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class MobileNotification {
        private NotifyType type;
        private Long targetUserId;
        private String title;
        private String content;
        private String targetType;
        private String targetId;
        private Map<String, Object> extra;
        private LocalDateTime timestamp;

        public enum NotifyType {
            SHOP_AUDIT_RESULT,
            CAR_STATUS_CHANGED,
            DISPUTE_RESOLVED,
            DEPOSIT_CHANGED,
            USER_STATUS_CHANGED,
            SYSTEM_ANNOUNCEMENT,
            PURCHASE_MATCHED,
            ORDER_STATUS_CHANGED
        }
    }

    /**
     * 消息监听器适配器
     */
    private static class MessageListenerAdapter implements MessageListener {
        private final MobileEventListener listener;
        private final String methodName;

        public MessageListenerAdapter(MobileEventListener listener, String methodName) {
            this.listener = listener;
            this.methodName = methodName;
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            try {
                String body = new String(message.getBody(), "UTF-8");
                // 通过反射调用指定方法
                java.lang.reflect.Method method = listener.getClass().getDeclaredMethod(methodName, String.class);
                method.invoke(listener, body);
            } catch (Exception e) {
                log.error("[MobileEventListener] Failed to invoke handler method", e);
            }
        }
    }
}
