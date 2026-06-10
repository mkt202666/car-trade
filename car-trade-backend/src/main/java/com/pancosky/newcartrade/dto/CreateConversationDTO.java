package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 创建聊天会话请求 DTO
 * 描述：用户发起与卖家/客服的聊天会话，或绑定订单上下文时使用。
 * 使用场景：POST /api/chat/conversations 接口的请求体。
 * 注意：targetUserId 为会话对方用户；若同时传入 orderId，则会话与该订单绑定。
 */
@Data
public class CreateConversationDTO {

    /** 对方用户ID（聊天对象，如卖家、客服；关联 users.id） */
    private Long targetUserId;

    /** 卖家用户ID（冗余字段，便于后台识别所属卖家；可与 targetUserId 相同） */
    private Long sellerId;

    /** 关联订单号（若会话因某订单发起，用于展示订单卡片；关联 orders.id） */
    private String orderId;
}
