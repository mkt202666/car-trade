package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class NotificationQueryDTO {
    private String keyword;
    private String type;
    private Long targetUserId;
    private String status;
    private int page = 1;
    private int size = 20;
}
