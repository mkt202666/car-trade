package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {
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
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer year;
    private Integer mileage;
    private String color;
    private String city;
    private String overallCondition;
    private String paint;
    private String structure;
    private String engine;
    private String transmission;
    private Integer transferCount;
    private String mileageType;
    private String materials;
    private String contractNo;
    private String contractStatus;
    private Boolean buyerDepositPaid;
    private Boolean sellerDepositPaid;
    private String depositStatus;
    private String cancelReason;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
}
