package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardApprovalVO {
    private String type;
    private Object id;
    private String title;
    private LocalDateTime createdAt;
}
