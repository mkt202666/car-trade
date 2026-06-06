package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarVO {
    private Long id;
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer year;
    private Integer mileage;
    private BigDecimal price;
    private BigDecimal deposit;
    private String city;
    private String energyType;
    private List<String> images;
    private List<String> tags;
    private String auctionStatus;
    private LocalDateTime auctionEndTime;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Integer favoriteCount;
}
