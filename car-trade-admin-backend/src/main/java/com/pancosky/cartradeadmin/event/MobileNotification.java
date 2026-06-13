package com.pancosky.cartradeadmin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 移动端推送通知 DTO
 * 通过 Redis Pub/Sub 发布到 mobile:events 通道
 * 移动端(car-trade-backend)订阅此通道，将事件转化为 WebSocket 推送给用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileNotification {

    /** 通知类型 */
    private NotifyType type;

    /** 目标用户ID（接收通知的用户） */
    private Long targetUserId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 关联实体类型: car_source, order, dispute, deposit, shop, purchase, system */
    private String targetType;

    /** 关联实体ID */
    private String targetId;

    /** 附加数据 */
    private Map<String, Object> extra;

    /** 发送时间 */
    private LocalDateTime timestamp;

    /** 通知类型枚举 */
    public enum NotifyType {
        SHOP_AUDIT_RESULT,    // 车行审核结果
        CAR_STATUS_CHANGED,    // 车源状态变更
        DISPUTE_RESOLVED,      // 争议处理结果
        DEPOSIT_CHANGED,       // 保证金变动
        USER_STATUS_CHANGED,   // 用户状态变更（封禁/解封）
        SYSTEM_ANNOUNCEMENT,   // 系统公告
        PURCHASE_MATCHED,      // 求购匹配
        ORDER_STATUS_CHANGED   // 订单状态变更（运营端操作）
    }
}
