package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {
    private String id;
    private String carTitle;
    private Long carId;
    private Long buyerId;
    private Long sellerId;
    private String buyerName;
    private String buyerPhone;
    private String sellerName;
    private String sellerPhone;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String status;
    private Boolean buyerDepositPaid;
    private Boolean sellerDepositPaid;
    private String contractNo;
    private Boolean contractSubmitted;
    private Boolean contractConfirmed;
    private String remark;
    private String cancelReason;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 车辆信息 ---
    private String carName;
    private String carImage;
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer year;
    private Integer mileage;
    private String vin;
    private String color;
    private String city;
    private String registrationDate;
    private String insuranceExpiry;
    private String inspectionExpiry;
    private String energyType;

    // --- 检测信息 ---
    private String overallCondition;
    private String paint;
    private String structure;
    private String engine;
    private String transmission;
    private Integer transferCount;
    private String mileageType;
    private String materials;

    // --- 派生状态 ---
    private String contractStatus;
    private String depositStatus;
}
