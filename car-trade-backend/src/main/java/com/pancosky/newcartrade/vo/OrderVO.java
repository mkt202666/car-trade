package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private String id;
    private Long carId;
    private String carName;
    private String carImage;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String status;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
