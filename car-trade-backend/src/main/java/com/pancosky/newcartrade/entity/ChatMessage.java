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
 * 聊天消息实体类
 * 描述：聊天会话中的单条消息，支持文本、图片、文件、系统提示等多种类型。
 * 关联：外键 conversationId → chat_conversations；senderId → users。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("chat_messages")
public class ChatMessage {

    /** 消息ID（主键，PostgreSQL BIGSERIAL 自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属会话ID（关联 chat_conversations.id） */
    private Long conversationId;

    /** 发送人ID（关联 users.id；系统消息可设为 0/null） */
    private Long senderId;

    /** 消息正文（文本内容、文件URL、图片URL、卡片JSON等，按 messageType 解析） */
    private String content;

    /** 消息类型（TEXT=文本；IMAGE=图片；FILE=文件；SYSTEM=系统提示；CARD=卡片消息） */
    private String messageType;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}