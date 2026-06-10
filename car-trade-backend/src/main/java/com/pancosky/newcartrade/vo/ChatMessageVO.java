package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息VO
 * 描述：会话详情页滚动展示的单条聊天消息。
 */
@Data
public class ChatMessageVO {

    /** 消息ID */
    private Long id;

    /** 所属会话ID */
    private Long conversationId;

    /** 发送者用户ID（为 0 / null 表示系统消息） */
    private Long senderId;

    /** 消息文本内容（或系统消息文本） */
    private String content;

    /** 消息类型（如 TEXT 文本 / IMAGE 图片 / ORDER 订单卡片 / SYSTEM 系统消息） */
    private String messageType;

    /** 消息发送时间 */
    private LocalDateTime createdAt;
}
