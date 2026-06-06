package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditVO {
    private BigDecimal creditLimit;
    private BigDecimal usedAmount;
    private BigDecimal availableAmount;
}
