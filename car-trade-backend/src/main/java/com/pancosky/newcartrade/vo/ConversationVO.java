package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVO {
    private Long id;
    private String type;
    private String name;
    private String relatedOrderId;
    private Long targetUserId;
    private String targetUserName;
    private String targetUserAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
    private LocalDateTime createdAt;
}
