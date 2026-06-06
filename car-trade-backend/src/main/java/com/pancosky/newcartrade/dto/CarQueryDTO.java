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
    private String sort;
    private int page;
    private int size;
}
