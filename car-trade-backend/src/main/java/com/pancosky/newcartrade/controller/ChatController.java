package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.CreateConversationDTO;
import com.pancosky.newcartrade.service.ChatService;
import com.pancosky.newcartrade.vo.ChatMessageVO;
import com.pancosky.newcartrade.vo.ConversationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationVO>> listConversations() {
        return ApiResponse.success(chatService.listConversations());
    }

    @PostMapping("/conversations")
    public ApiResponse<ConversationVO> createConversation(@RequestBody CreateConversationDTO dto) {
        return ApiResponse.success(chatService.createConversation(dto));
    }

    @GetMapping("/conversations/{id}/messages")
    public ApiResponse<List<ChatMessageVO>> messages(@PathVariable Long id) {
        return ApiResponse.success(chatService.listMessages(id));
    }

    @PostMapping("/conversations/{id}/messages")
    public ApiResponse<ChatMessageVO> sendMessage(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(chatService.sendMessage(id, data));
    }

    @PostMapping("/conversations/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        chatService.markRead(id);
        return ApiResponse.success();
    }
}
