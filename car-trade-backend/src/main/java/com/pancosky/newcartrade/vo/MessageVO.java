package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private Long senderId;
    private String relatedId;
    private String relatedType;
    private LocalDateTime createdAt;
}
