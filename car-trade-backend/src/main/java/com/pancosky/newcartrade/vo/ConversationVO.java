package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话VO
 * 描述：消息/聊天页会话列表项的基本信息，包含对方用户、最近消息、未读数等。
 */
@Data
public class ConversationVO {

    /** 会话ID */
    private Long id;

    /** 会话类型（如 PRIVATE 私聊 / ORDER 订单相关 / GROUP 群聊） */
    private String type;

    /** 会话名称（群聊或订单相关会话展示用） */
    private String name;

    /** 关联订单ID（若为订单相关会话则填充） */
    private String relatedOrderId;

    /** 对方用户ID */
    private Long targetUserId;

    /** 对方用户名称 */
    private String targetUserName;

    /** 对方用户头像URL */
    private String targetUserAvatar;

    /** 最近一条消息文本内容 */
    private String lastMessage;

    /** 最近一条消息时间 */
    private LocalDateTime lastMessageAt;

    /** 未读消息数量 */
    private Integer unreadCount;

    /** 会话创建时间 */
    private LocalDateTime createdAt;
}
