package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardWarningVO {
    private Object id;
    private String type;
    private String level;
    private String message;
    private LocalDateTime createdAt;
}
