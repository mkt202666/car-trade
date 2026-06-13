package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositManualDTO {

    @NotNull(message = "userId不能为空")
    private Long userId;

    @NotNull(message = "type不能为空")
    private String type;

    @NotNull(message = "amount不能为空")
    @Positive(message = "金额必须为正数")
    private BigDecimal amount;

    private String remark;
}
