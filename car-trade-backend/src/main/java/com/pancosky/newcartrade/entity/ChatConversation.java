package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 * 描述：两个或多个用户之间的实时聊天会话根记录，承载消息列表。
 *       与 chat_conversation_members（成员）、chat_messages（消息）组成完整聊天模型。
 * 关联：relatedOrderId → orders（可选，会话绑定订单上下文）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("chat_conversations")
public class ChatConversation {

    /** 会话ID（主键，PostgreSQL BIGSERIAL 自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话类型（PRIVATE=私聊；GROUP=群聊；ORDER_CHAT=订单会话；SUPPORT=客服会话） */
    private String type;

    /** 关联订单ID（当 type=ORDER_CHAT 时使用，标识会话属于某订单上下文） */
    private String relatedOrderId;

    /** 最后一条消息预览（用于会话列表的"最后一条消息"展示） */
    private String lastMessage;

    /** 最后一条消息时间（用于会话列表的时间排序） */
    private LocalDateTime lastMessageAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
