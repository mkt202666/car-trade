package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepositRecordVO {
    private Long id;
    private Long userId;
    private String userName;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String remark;
    private LocalDateTime createdAt;
}
