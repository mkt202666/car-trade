package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarVO {
    private Long id;
    private String title;
    private Integer brandId;
    private Integer seriesId;
    private String cityName;
    private String energyType;
    private BigDecimal price;
    private Integer mileage;
    private Integer year;
    private String sellerName;
    private String sellerPhone;
    private String status;
    private Long viewCount;
    private LocalDateTime createdAt;
}
