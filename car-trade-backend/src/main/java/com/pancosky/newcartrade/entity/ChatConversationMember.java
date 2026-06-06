package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("chat_conversation_members")
public class ChatConversationMember {
    @TableId
    private Long id;
    private Long conversationId;
    private Long userId;
    private Integer unreadCount;
    private LocalDateTime lastReadAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
