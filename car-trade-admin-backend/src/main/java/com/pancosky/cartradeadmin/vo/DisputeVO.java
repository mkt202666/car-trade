package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DisputeVO {
    private Long id;
    private String orderId;
    private String orderTitle;
    private String initiatorName;
    private String initiatorPhone;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}
