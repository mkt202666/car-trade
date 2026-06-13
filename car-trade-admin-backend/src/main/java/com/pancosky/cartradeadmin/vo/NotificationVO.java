package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private String type;
    private Long targetUserId;
    private String targetUserName;
    private String title;
    private String content;
    private String targetType;
    private String targetId;
    private String status;
    private LocalDateTime createdAt;
}
