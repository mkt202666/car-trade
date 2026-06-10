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

/**
 * 聊天会话控制器
 * 描述：提供聊天会话列表、创建会话、发送/查看消息以及标记已读等接口。
 * 基础路径：/api/v1/chat
 * 认证要求：全部接口必须登录。
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 我的会话列表
     * HTTP: GET /api/v1/chat/conversations
     * 响应：ApiResponse&lt;List&lt;ConversationVO&gt;&gt; —— 按最后一条消息时间倒序的会话列表，含未读数。
     * 认证要求：必须登录。
     */
    @GetMapping("/conversations")
    public ApiResponse<List<ConversationVO>> listConversations() {
        return ApiResponse.success(chatService.listConversations());
    }

    /**
     * 创建/打开一个会话
     * HTTP: POST /api/v1/chat/conversations
     * 请求参数：CreateConversationDTO（JSON body）—— targetUserId、sellerId、orderId（可选）。
     * 响应：ApiResponse&lt;ConversationVO&gt; —— 返回会话信息（若已存在则返回原会话）。
     * 认证要求：必须登录。
     */
    @PostMapping("/conversations")
    public ApiResponse<ConversationVO> createConversation(@RequestBody CreateConversationDTO dto) {
        return ApiResponse.success(chatService.createConversation(dto));
    }

    /**
     * 查看会话消息列表
     * HTTP: GET /api/v1/chat/conversations/{id}/messages
     * 请求参数：id（path，会话ID）
     * 响应：ApiResponse&lt;List&lt;ChatMessageVO&gt;&gt; —— 按时间正序的消息历史。
     * 认证要求：必须登录；必须是会话成员。
     */
    @GetMapping("/conversations/{id}/messages")
    public ApiResponse<List<ChatMessageVO>> messages(@PathVariable Long id) {
        return ApiResponse.success(chatService.listMessages(id));
    }

    /**
     * 发送消息
     * HTTP: POST /api/v1/chat/conversations/{id}/messages
     * 请求参数：id（path，会话ID）；body（JSON，内容字段按业务约定：content、type、fileUrl 等）。
     * 响应：ApiResponse&lt;ChatMessageVO&gt; —— 返回发送成功的消息对象。
     * 认证要求：必须登录；必须是会话成员。
     * 限流：单用户每秒最多发送 5 条消息。
     */
    @PostMapping("/conversations/{id}/messages")
    public ApiResponse<ChatMessageVO> sendMessage(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(chatService.sendMessage(id, data));
    }

    /**
     * 标记会话已读
     * HTTP: POST /api/v1/chat/conversations/{id}/read
     * 请求参数：id（path，会话ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；必须是会话成员。
     */
    @PostMapping("/conversations/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        chatService.markRead(id);
        return ApiResponse.success();
    }
}
