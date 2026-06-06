package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CarCreateDTO {
    private Integer brandId;
    private Integer seriesId;
    private Integer modelId;
    private Integer year;
    private Integer mileage;
    private BigDecimal price;
    private BigDecimal deposit;
    private String color;
    private String cityCode;
    private String usageType;
    private String ownerType;
    private Boolean isMortgaged;
    private Boolean isInherited;
    private String description;
    private List<String> images;
    private CarInspectionDTO inspection;
    private List<String> exportCountries;
}
