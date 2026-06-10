package com.pancosky.newcartrade.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnBean(org.apache.rocketmq.spring.core.RocketMQTemplate.class)
public class MessageConsumer {

    private final ObjectMapper objectMapper;
    private final PushManager pushManager;

    public MessageConsumer(ObjectMapper objectMapper, PushManager pushManager) {
        this.objectMapper = objectMapper;
        this.pushManager = pushManager;
    }

    @Component
    @RocketMQMessageListener(
        topic = "message-system",
        consumerGroup = "new-car-trade-system-consumer",
        accessChannel = "CLOUD",
        namespaceV2 = "rmq-cn-zqb4angv602"
    )
    public class SystemMessageConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String payload) {
            try {
                Message message = objectMapper.readValue(payload, Message.class);
                log.info("Received system message: {}", message.getTitle());
                MessageVO vo = new MessageVO();
                vo.setId(message.getId());
                vo.setType(message.getType());
                vo.setTitle(message.getTitle());
                vo.setContent(message.getContent());
                vo.setIsRead(message.getIsRead());
                vo.setSenderId(message.getSenderId());
                vo.setRelatedId(message.getRelatedId());
                vo.setRelatedType(message.getRelatedType());
                vo.setCreatedAt(message.getCreatedAt());
                pushManager.pushToUser(message.getUserId(), vo);
            } catch (Exception e) {
                log.error("Failed to process system message", e);
            }
        }
    }

    @Component
    @RocketMQMessageListener(
        topic = "message-chat",
        consumerGroup = "new-car-trade-chat-consumer",
        accessChannel = "CLOUD",
        namespaceV2 = "rmq-cn-zqb4angv602"
    )
    public class ChatMessageConsumer implements RocketMQListener<String> {
        @Override
        public void onMessage(String payload) {
            try {
                Message message = objectMapper.readValue(payload, Message.class);
                log.info("Received chat message from user: {}", message.getSenderId());
                MessageVO vo = new MessageVO();
                vo.setId(message.getId());
                vo.setType(message.getType());
                vo.setTitle(message.getTitle());
                vo.setContent(message.getContent());
                vo.setIsRead(message.getIsRead());
                vo.setSenderId(message.getSenderId());
                vo.setRelatedId(message.getRelatedId());
                vo.setRelatedType(message.getRelatedType());
                vo.setCreatedAt(message.getCreatedAt());
                pushManager.pushToUser(message.getUserId(), vo);
            } catch (Exception e) {
                log.error("Failed to process chat message", e);
            }
        }
    }
}