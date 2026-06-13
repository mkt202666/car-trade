package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.MessageService;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内消息/通知控制器
 * 描述：提供消息列表、未读数、标记已读、全部已读、删除消息等接口。
 * 基础路径：/api/v1/messages
 * 认证要求：全部接口必须登录；所有消息基于当前用户。
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PROTECTED)
public class MessageController {

    private final MessageService messageService;

    /**
     * 消息列表
     * HTTP: GET /api/v1/messages?type=ORDER&isRead=false
     * 请求参数：type（过滤类型：ORDER/COUPON/AUCTION/SYSTEM 等）、isRead（布尔，是否已读过滤）。
     * 响应：ApiResponse&lt;List&lt;MessageVO&gt;&gt; —— 按创建时间倒序的消息列表。
     * 认证要求：必须登录。
     */
    @GetMapping
    public ApiResponse<List<MessageVO>> list(@RequestParam(required = false) String type,
                                             @RequestParam(required = false) Boolean isRead) {
        return ApiResponse.success(messageService.list(type, isRead));
    }

    /**
     * 未读消息数
     * HTTP: GET /api/v1/messages/unread-count
     * 响应：ApiResponse&lt;Long&gt; —— 当前用户未读消息总数。
     * 认证要求：必须登录。
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.success(messageService.unreadCount());
    }

    /**
     * 标记单条消息已读
     * HTTP: PUT /api/v1/messages/{id}/read
     * 请求参数：id（path，消息ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return ApiResponse.success();
    }

    /**
     * 一键标记所有消息已读
     * HTTP: PUT /api/v1/messages/read-all
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        messageService.markAllRead();
        return ApiResponse.success();
    }

    /**
     * 删除消息
     * HTTP: DELETE /api/v1/messages/{id}
     * 请求参数：id（path，消息ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；仅删除自己的消息记录（软删除）。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 获取通知订阅设置
     * HTTP: GET /api/v1/messages/notification-settings
     * 响应：ApiResponse&lt;String&gt; —— JSON格式的订阅设置
     */
    @GetMapping("/notification-settings")
    public ApiResponse<String> getNotificationSettings() {
        return ApiResponse.success(messageService.getNotificationSettings());
    }

    /**
     * 更新通知订阅设置
     * HTTP: PUT /api/v1/messages/notification-settings
     * 请求体：JSON字符串 {"system":true,"auto_promotion":true,...}
     * 响应：ApiResponse&lt;Void&gt;
     */
    @PutMapping("/notification-settings")
    public ApiResponse<Void> updateNotificationSettings(@RequestBody String settings) {
        messageService.updateNotificationSettings(settings);
        return ApiResponse.success();
    }
}
