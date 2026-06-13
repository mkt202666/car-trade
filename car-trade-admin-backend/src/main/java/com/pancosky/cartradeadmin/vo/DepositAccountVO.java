package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositAccountVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private BigDecimal totalDeposit;
    private String status;
}
