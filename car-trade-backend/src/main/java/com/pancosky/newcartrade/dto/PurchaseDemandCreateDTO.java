package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDemandCreateDTO {
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer yearFrom;
    private Integer yearTo;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private Integer mileageMax;
    private String color;
    private String cityCode;
    private String cityName;
    private String energyType;
    private String description;
}
