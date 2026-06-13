package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardCouponVO {
    private int totalCount;
    private int usedCount;
    private int remainCount;
    private BigDecimal usageRate;
}
