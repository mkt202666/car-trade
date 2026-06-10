package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarQueryDTO {
    private String keyword;
    private Integer brandId;
    private Integer seriesId;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private BigDecimal minPrice; // 兼容前端参数
    private BigDecimal maxPrice; // 兼容前端参数
    private Integer ageMin;
    private Integer ageMax;
    private Integer minAge; // 兼容前端参数
    private Integer maxAge; // 兼容前端参数
    private BigDecimal mileageMin;
    private BigDecimal mileageMax;
    private BigDecimal minMileage; // 兼容前端参数
    private BigDecimal maxMileage; // 兼容前端参数
    private String transmission;
    private String cityCode;
    private String energyType;
    private String exportCountry;
    private String region;
    private Boolean deposit;
    /** 排序: price_asc / price_desc / mileage_asc / created_desc (默认) */
    private String sort;
    private Integer page = 1;
    private Integer size = 10;
}
