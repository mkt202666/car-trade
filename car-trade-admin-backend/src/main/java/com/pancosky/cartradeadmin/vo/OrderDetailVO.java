package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailVO {
    private String id;
    private String carTitle;
    private Long carId;
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
}
