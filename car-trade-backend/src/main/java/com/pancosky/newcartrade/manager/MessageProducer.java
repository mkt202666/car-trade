package com.pancosky.newcartrade.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(RocketMQTemplate.class)
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    public MessageProducer(RocketMQTemplate rocketMQTemplate, ObjectMapper objectMapper) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendSystemMessage(Message message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            rocketMQTemplate.send("message-system", MessageBuilder.withPayload(payload).build());
        } catch (Exception e) {
            log.error("Failed to send system message", e);
        }
    }

    public void sendChatMessage(Message message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            String hashKey = String.valueOf(message.getUserId());
            rocketMQTemplate.syncSendOrderly("message-chat", MessageBuilder.withPayload(payload).build(), hashKey);
        } catch (Exception e) {
            log.error("Failed to send chat message", e);
        }
    }
}
