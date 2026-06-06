package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDetailVO {
    private Long id;
    private String title;
    private String category;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private Long userId;
    private Long handlerId;
    private LocalDateTime handledAt;
    private List<String> messages;
}
