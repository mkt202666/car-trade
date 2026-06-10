package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金流水记录VO
 * 描述：保证金账户的资金流水列表项，用于充值/扣款/解冻等操作记录展示。
 */
@Data
public class DepositRecordVO {

    /** 流水记录ID */
    private Long id;

    /** 流水类型（RECHARGE 充值 / FREEZE 冻结 / UNFREEZE 解冻 / DEDUCT 扣除 / REFUND 退回） */
    private String type;

    /** 本次变动金额（元），正数表示资金流入账户，负数表示流出 */
    private BigDecimal amount;

    /** 变动后账户余额（元） */
    private BigDecimal balanceAfter;

    /** 流水备注说明（如"订单 OD202507150001 保证金冻结"） */
    private String remark;

    /** 流水创建时间 */
    private LocalDateTime createdAt;
}
