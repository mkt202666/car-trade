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
    private String cityCode;
    private String energyType;
    private String exportCountry;
    private Boolean deposit;
    /** 排序: price_asc / price_desc / mileage_asc / created_desc (默认) */
    private String sort;
    private Integer page = 1;
    private Integer size = 10;
}
