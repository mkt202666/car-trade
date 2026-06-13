package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseRequestVO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private String brandName;
    private String seriesName;
    private String modelName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer yearMin;
    private Integer yearMax;
    private String cityName;
    private String energyType;
    private String status;
    private LocalDateTime createdAt;
}
