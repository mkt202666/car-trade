package com.pancosky.cartradeadmin.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MobileEventPublisher {

    private static final String CHANNEL = "mobile:events";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public MobileEventPublisher() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void publish(MobileNotification notification) {
        if (notification.getTimestamp() == null) {
            notification.setTimestamp(LocalDateTime.now());
        }
        try {
            String json = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(CHANNEL, json);
            log.info("[MobileEvent] Published to mobile: type={}, targetUser={}",
                    notification.getType(), notification.getTargetUserId());
        } catch (JsonProcessingException e) {
            log.error("[MobileEvent] Failed to serialize notification", e);
        } catch (Exception e) {
            log.error("[MobileEvent] Failed to publish to Redis channel '{}'", CHANNEL, e);
        }
    }

    /** 便捷方法：发送通知 */
    public void send(MobileNotification.NotifyType type, Long targetUserId,
                     String title, String content,
                     String targetType, String targetId) {
        publish(MobileNotification.builder()
                .type(type)
                .targetUserId(targetUserId)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .build());
    }
}
