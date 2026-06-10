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
 * 保证金账户实体类
 * 描述：用户在平台的保证金钱包，用于下单/出价保证金冻结、交易完成解冻/扣罚。
 * 关联：外键 userId → users；通过 deposit_records 记录每笔资金变动。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("deposit_accounts")
public class DepositAccount {

    /** 保证金账户ID（主键） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id，一个用户一个保证金账户） */
    private Long userId;

    /** 可用余额（单位：元，可用于提现或新的保证金冻结） */
    private BigDecimal balance;

    /** 冻结金额（单位：元，当前被订单/拍卖占用的保证金合计） */
    private BigDecimal frozenAmount;

    /** 累计充值总额（用于风控/数据统计） */
    private BigDecimal totalDeposit;

    /** 账户状态（ACTIVE=正常；FROZEN=冻结；CLOSED=已关闭） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
