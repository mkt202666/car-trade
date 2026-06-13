package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private String id;
    private String carTitle;
    private Long carId;
    private String buyerName;
    private String sellerName;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String status;
    private LocalDateTime createdAt;
}
