package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardKpiVO {
    // 基础统计
    private long userCount;
    private long shopCount;
    private long carCount;
    private long orderCount;
    private BigDecimal tradeAmount;
    private long pendingReviewCount;
    private long pendingDisputeCount;
    private long todayNewUsers;
    private long todayNewCars;
    private long todayOrders;

    // 前端 Dashboard KPI 卡片需要的字段
    private BigDecimal gmv;           // 累计GMV（所有已完成订单交易额）
    private String gmvTrend;          // GMV环比趋势描述
    private long deposit;             // 冻结中投标保证金总额
    private long depositActive;       // 活跃保证金账户数
    private long pendingProcessed;    // 已处理认证数
}
