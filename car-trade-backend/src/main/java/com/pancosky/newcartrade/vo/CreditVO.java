package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 信用额度VO
 * 描述：用户信用额度账户信息，用于信用支付、信用额度管理展示。
 */
@Data
public class CreditVO {

    /** 总信用额度（元，由用户信用等级决定） */
    private BigDecimal creditLimit;

    /** 已使用金额（当前订单中占用的信用额度） */
    private BigDecimal usedAmount;

    /** 可用额度（creditLimit - usedAmount） */
    private BigDecimal availableAmount;
}
