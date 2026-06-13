package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("deposit_records")
public class AppDepositRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long accountId;

    private Long userId;

    /** CHARGE / WITHDRAW / FREEZE / UNFREEZE / REFUND / MANUAL */
    private String type;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String remark;

    private Long operatorId;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
