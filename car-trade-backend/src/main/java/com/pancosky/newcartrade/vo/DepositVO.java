package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 保证金账户VO
 * 描述：用户保证金账户余额信息，用于保证金管理页展示。
 */
@Data
public class DepositVO {

    /** 账户可用余额（元） */
    private BigDecimal balance;

    /** 已冻结金额（正在参与的拍卖/订单占用金额） */
    private BigDecimal frozenAmount;

    /** 累计充值总额（用于展示统计） */
    private BigDecimal totalDeposit;
}
