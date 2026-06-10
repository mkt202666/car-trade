package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.entity.ChatConversation;
import com.pancosky.newcartrade.entity.ChatConversationMember;
import com.pancosky.newcartrade.entity.ChatMessage;
import com.pancosky.newcartrade.mapper.ChatConversationMapper;
import com.pancosky.newcartrade.mapper.ChatConversationMemberMapper;
import com.pancosky.newcartrade.mapper.ChatMessageMapper;
import com.pancosky.newcartrade.service.ChatService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationMapper conversationMapper;
    private final ChatConversationMemberMapper conversationMemberMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ConversationVO> listConversations() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ChatConversationMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ChatConversationMember::getUserId, userId);
        List<ChatConversationMember> members = conversationMemberMapper.selectList(memberWrapper);
        List<Long> conversationIds = members.stream()
                .map(ChatConversationMember::getConversationId)
                .collect(Collectors.toList());
        if (conversationIds.isEmpty()) return List.of();
        List<ChatConversation> conversations = conversationMapper.selectBatchIds(conversationIds);
        return conversations.stream().map(conv -> {
            ConversationVO vo = new ConversationVO();
            vo.setId(conv.getId());
            vo.setType(conv.getType());
            vo.setRelatedOrderId(conv.getRelatedOrderId());
            vo.setCreatedAt(conv.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationVO createConversation(CreateConversationDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        ChatConversation conversation = new ChatConversation();
        conversation.setType("CHAT");
        conversation.setRelatedOrderId(dto.getOrderId());
        conversationMapper.insert(conversation);
        ChatConversationMember member = new ChatConversationMember();
        member.setConversationId(conversation.getId());
        member.setUserId(userId);
        conversationMemberMapper.insert(member);
        Long targetId = dto.getTargetUserId() != null ? dto.getTargetUserId() : dto.getSellerId();
        if (targetId != null) {
            ChatConversationMember target = new ChatConversationMember();
            target.setConversationId(conversation.getId());
            target.setUserId(targetId);
            conversationMemberMapper.insert(target);
        }
        ConversationVO vo = new ConversationVO();
        vo.setId(conversation.getId());
        vo.setType(conversation.getType());
        vo.setRelatedOrderId(conversation.getRelatedOrderId());
        vo.setCreatedAt(conversation.getCreatedAt());
        vo.setName("在线客服");
        return vo;
    }

    @Override
    public List<ChatMessageVO> listMessages(Long conversationId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId);
        wrapper.orderByAsc(ChatMessage::getCreatedAt);
        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        return messages.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageVO sendMessage(Long conversationId, Map<String, Object> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        String content = (String) data.get("content");
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(userId);
        message.setContent(content);
        message.setMessageType("TEXT");
        chatMessageMapper.insert(message);
        
        ChatMessageVO vo = toVO(message);
        log.info("User {} sent message to conversation {}: {}", userId, conversationId, content);
        return vo;
    }

    @Override
    public void markRead(Long conversationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Mark conversation {} as read for user {}", conversationId, userId);
    }

    private ChatMessageVO toVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderId(message.getSenderId());
        vo.setContent(message.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setCreatedAt(message.getCreatedAt());
        return vo;
    }
}
