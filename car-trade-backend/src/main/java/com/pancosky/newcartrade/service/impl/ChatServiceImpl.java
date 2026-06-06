package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.entity.ChatConversation;
import com.pancosky.newcartrade.entity.ChatConversationMember;
import com.pancosky.newcartrade.mapper.ChatConversationMapper;
import com.pancosky.newcartrade.mapper.ChatConversationMemberMapper;
import com.pancosky.newcartrade.service.ChatService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationMapper conversationMapper;
    private final ChatConversationMemberMapper conversationMemberMapper;

    @Override
    public List<ConversationVO> listConversations() {
        return null;
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
        ChatConversationMember target = new ChatConversationMember();
        target.setConversationId(conversation.getId());
        target.setUserId(dto.getTargetUserId());
        conversationMemberMapper.insert(target);
        return null;
    }

    @Override
    public List<ChatMessageVO> listMessages(Long conversationId) {
        return null;
    }

    @Override
    public void markRead(Long conversationId) {
        Long userId = SecurityUtils.getCurrentUserId();
    }
}
