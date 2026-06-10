package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.manager.MessageProducer;
import com.pancosky.newcartrade.mapper.MessageMapper;
import com.pancosky.newcartrade.service.MessageService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Autowired(required = false)
    private MessageProducer messageProducer;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public List<MessageVO> list(String type, Boolean isRead) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        if (StringUtils.hasText(type)) {
            wrapper.eq(Message::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }
        wrapper.orderByDesc(Message::getCreatedAt);
        List<Message> messages = messageMapper.selectList(wrapper);
        return messages.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public long unreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        wrapper.eq(Message::getIsRead, false);
        return messageMapper.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void markRead(Long id) {
        Message message = messageMapper.selectById(id);
        if (message != null) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        wrapper.eq(Message::getIsRead, false);
        List<Message> messages = messageMapper.selectList(wrapper);
        for (Message message : messages) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    public void sendSystem(Message message) {
        messageMapper.insert(message);
        if (messageProducer != null) {
            messageProducer.sendSystemMessage(message);
        } else {
            log.debug("RocketMQ not available, system message saved to DB only");
        }
    }

    @Override
    public void sendChat(Message message) {
        messageMapper.insert(message);
        if (messageProducer != null) {
            messageProducer.sendChatMessage(message);
        } else {
            log.debug("RocketMQ not available, chat message saved to DB only");
        }
    }

    private MessageVO toVO(Message msg) {
        MessageVO vo = new MessageVO();
        vo.setId(msg.getId());
        vo.setType(msg.getType());
        vo.setTitle(msg.getTitle());
        vo.setContent(msg.getContent());
        vo.setIsRead(msg.getIsRead());
        vo.setSenderId(msg.getSenderId());
        vo.setRelatedId(msg.getRelatedId());
        vo.setRelatedType(msg.getRelatedType());
        vo.setCreatedAt(msg.getCreatedAt());
        return vo;
    }
}
