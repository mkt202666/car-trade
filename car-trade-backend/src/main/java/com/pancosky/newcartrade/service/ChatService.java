package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;

import java.util.List;

public interface ChatService {
    List<ConversationVO> listConversations();
    ConversationVO createConversation(CreateConversationDTO dto);
    List<ChatMessageVO> listMessages(Long conversationId);
    void markRead(Long conversationId);
}
