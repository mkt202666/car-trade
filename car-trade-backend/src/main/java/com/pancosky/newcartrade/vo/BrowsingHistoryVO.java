package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BrowsingHistoryVO {
    private Long id;
    private Long carId;
    private String carName;
    private String carImage;
    private BigDecimal price;
    private Integer mileage;
    private String city;
    private LocalDateTime createdAt;
}
