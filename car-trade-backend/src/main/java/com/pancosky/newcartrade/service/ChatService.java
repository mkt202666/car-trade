package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;

import java.util.List;
import java.util.Map;

public interface ChatService {
    List<ConversationVO> listConversations();
    ConversationVO createConversation(CreateConversationDTO dto);
    List<ChatMessageVO> listMessages(Long conversationId);
    ChatMessageVO sendMessage(Long conversationId, Map<String, Object> data);
    void markRead(Long conversationId);
}
