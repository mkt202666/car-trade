package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketVO {
    private Long id;
    private String title;
    private String category;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
}
