package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderLogVO {
    private Long id;
    private String orderId;
    private String operatorName;
    private String action;
    private String detail;
    private LocalDateTime createdAt;
}
