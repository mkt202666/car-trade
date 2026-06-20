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
 * 聊天会话成员关联实体类
 * 描述：每一个聊天会话所包含的成员及其未读消息数、已读时间等状态。
 * 关联：外键 conversationId → chat_conversations；userId → users。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_chat_conversation_members")
public class ChatConversationMember {

    /** 记录ID（主键，PostgreSQL BIGSERIAL 自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属会话ID（关联 chat_conversations.id） */
    private Long conversationId;

    /** 成员用户ID（关联 users.id） */
    private Long userId;

    /** 该成员当前未读消息数（进入会话后清零，用于顶部消息小红点） */
    private Integer unreadCount;

    /** 最后已读时间（记录成员上次阅读消息的时间点，用于增量推送） */
    private LocalDateTime lastReadAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充；即成员加入会话时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
