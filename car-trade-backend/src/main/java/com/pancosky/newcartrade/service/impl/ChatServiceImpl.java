package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.entity.ChatConversation;
import com.pancosky.newcartrade.entity.ChatConversationMember;
import com.pancosky.newcartrade.entity.ChatMessage;
import com.pancosky.newcartrade.manager.PushManager;
import com.pancosky.newcartrade.mapper.ChatConversationMapper;
import com.pancosky.newcartrade.mapper.ChatConversationMemberMapper;
import com.pancosky.newcartrade.mapper.ChatMessageMapper;
import com.pancosky.newcartrade.service.ChatService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final PushManager pushManager;

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
            vo.setLastMessage(conv.getLastMessage());
            vo.setLastMessageAt(conv.getLastMessageAt());
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
        member.setUnreadCount(0);
        conversationMemberMapper.insert(member);
        Long targetId = dto.getTargetUserId() != null ? dto.getTargetUserId() : dto.getSellerId();
        if (targetId != null) {
            ChatConversationMember target = new ChatConversationMember();
            target.setConversationId(conversation.getId());
            target.setUserId(targetId);
            target.setUnreadCount(0);
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

        // 更新会话的最后消息
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setLastMessage(content);
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }

        // 增加其他成员的未读数
        LambdaQueryWrapper<ChatConversationMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(ChatConversationMember::getConversationId, conversationId)
                     .ne(ChatConversationMember::getUserId, userId);
        List<ChatConversationMember> otherMembers = conversationMemberMapper.selectList(memberWrapper);
        for (ChatConversationMember m : otherMembers) {
            m.setUnreadCount(m.getUnreadCount() + 1);
            conversationMemberMapper.updateById(m);
        }

        ChatMessageVO vo = toVO(message);

        // WebSocket 实时推送
        try {
            MessageVO pushMsg = new MessageVO();
            pushMsg.setType("CHAT");
            pushMsg.setTitle("新消息");
            pushMsg.setContent(content);
            pushMsg.setRelatedId(conversationId.toString());
            pushMsg.setRelatedType("CHAT");
            for (ChatConversationMember m : otherMembers) {
                pushManager.pushToUser(m.getUserId(), pushMsg);
            }
        } catch (Exception e) {
            log.warn("WebSocket push failed for conversation {}: {}", conversationId, e.getMessage());
        }

        return vo;
    }

    @Override
    @Transactional
    public void markRead(Long conversationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ChatConversationMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatConversationMember::getConversationId, conversationId)
               .eq(ChatConversationMember::getUserId, userId);
        ChatConversationMember member = conversationMemberMapper.selectOne(wrapper);
        if (member != null) {
            member.setUnreadCount(0);
            member.setLastReadAt(LocalDateTime.now());
            conversationMemberMapper.updateById(member);
        }
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
