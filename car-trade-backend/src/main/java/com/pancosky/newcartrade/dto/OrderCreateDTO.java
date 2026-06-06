package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateDTO {
    private Long carId;
    private BigDecimal depositAmount;
    private String remark;
}
