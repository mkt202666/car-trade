package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计VO
 */
@Data
public class OrderStatsVO {

    /** 待确认订单数 */
    private Integer pendingConfirmCount = 0;

    /** 交易中订单数 */
    private Integer tradingCount = 0;

    /** 争议中订单数 */
    private Integer disputeCount = 0;

    /** 已完成订单数 */
    private Integer completedCount = 0;

    /** 已取消/终止订单数 */
    private Integer cancelledCount = 0;

    /** 总订单数 */
    private Integer totalCount = 0;

    /** 总成交金额 */
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /** 待支付保证金订单数 */
    private Integer pendingDepositCount = 0;
}