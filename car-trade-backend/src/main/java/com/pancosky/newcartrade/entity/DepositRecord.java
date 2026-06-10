package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金流水记录实体类
 * 描述：保证金账户的每一笔资金变动流水，便于审计、对账单、财务回溯。
 * 关联：外键 accountId → deposit_accounts；orderId → orders（可选）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("deposit_records")
public class DepositRecord {

    /** 流水ID（主键） */
    @TableId
    private Long id;

    /** 保证金账户ID（关联 deposit_accounts.id） */
    private Long accountId;

    /** 关联订单号（若本次变动由订单触发，则记录 orders.id） */
    private String orderId;

    /** 流水类型（RECHARGE=充值；WITHDRAW=提现；FREEZE=订单冻结；
     *  UNFREEZE=解冻释放；DEDUCT=扣罚保证金；REFUND=退款） */
    private String type;

    /** 变动金额（单位：元；正数表示入账，负数表示出账） */
    private BigDecimal amount;

    /** 变动后账户余额（用于幂等校验与余额核对） */
    private BigDecimal balanceAfter;

    /** 流水备注（描述变动原因，如"订单 ORDxxx 保证金冻结"） */
    private String remark;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
