package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.manager.MessageProducer;
import com.pancosky.newcartrade.mapper.MessageMapper;
import com.pancosky.newcartrade.service.MessageService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final MessageProducer messageProducer;

    @Override
    public List<MessageVO> list(String type, Boolean isRead) {
        Long userId = SecurityUtils.getCurrentUserId();
        return null;
    }

    @Override
    public long unreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return 0;
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
    }

    @Override
    @Transactional
    public void delete(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    public void sendSystem(Message message) {
        messageMapper.insert(message);
        messageProducer.sendSystemMessage(message);
    }

    @Override
    public void sendChat(Message message) {
        messageMapper.insert(message);
        messageProducer.sendChatMessage(message);
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
