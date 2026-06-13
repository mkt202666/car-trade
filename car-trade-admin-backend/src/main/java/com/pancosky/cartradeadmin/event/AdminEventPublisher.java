package com.pancosky.cartradeadmin.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publishes admin operation events to Redis Pub/Sub channel.
 * Channel: admin:events
 * Other services subscribe to this channel for real-time event processing.
 */
@Slf4j
@Component
public class AdminEventPublisher {

    private static final String CHANNEL = "admin:events";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public AdminEventPublisher() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Publish an admin event.
     */
    public void publish(AdminEvent event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(CHANNEL, json);
            log.info("[EventPublisher] Published event: type={}, target={}:{}",
                    event.getType(), event.getTargetType(), event.getTargetId());
        } catch (JsonProcessingException e) {
            log.error("[EventPublisher] Failed to serialize event: type={}", event.getType(), e);
        } catch (Exception e) {
            log.error("[EventPublisher] Failed to publish event to Redis channel '{}'", CHANNEL, e);
        }
    }

    /**
     * Convenience: build and publish a simple event.
     */
    public void publish(AdminEvent.EventType type, Long adminId, String adminName,
                        String targetType, String targetId, String description) {
        publish(AdminEvent.builder()
                .type(type)
                .adminId(adminId)
                .adminName(adminName)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .build());
    }
}
