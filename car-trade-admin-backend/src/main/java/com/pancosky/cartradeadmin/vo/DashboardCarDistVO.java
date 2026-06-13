package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardCarDistVO {
    private String channel;
    private long count;
    private BigDecimal percentage;
}
