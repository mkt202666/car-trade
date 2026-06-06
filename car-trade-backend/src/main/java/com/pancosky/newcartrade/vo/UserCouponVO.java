package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponVO {
    private Long id;
    private String couponName;
    private String type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private String status;
    private LocalDateTime endAt;
}
