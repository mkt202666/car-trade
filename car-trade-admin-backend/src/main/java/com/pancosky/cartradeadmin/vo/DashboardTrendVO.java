package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardTrendVO {
    private String date;
    private int orderCount;
    private BigDecimal tradeAmount;
    private int newUsers;
}
