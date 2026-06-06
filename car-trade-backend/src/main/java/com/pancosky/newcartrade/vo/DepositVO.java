package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositVO {
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private BigDecimal totalDeposit;
}
