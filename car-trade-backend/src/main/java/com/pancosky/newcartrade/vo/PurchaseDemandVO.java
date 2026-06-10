package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseDemandVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer yearFrom;
    private Integer yearTo;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Integer mileageMax;
    private String color;
    private String cityName;
    private String energyType;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
