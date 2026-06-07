package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private String cityName;
    private String energyType;
    private String usageType;
    private String ownerType;
    private Boolean isMortgaged;
    private Boolean isInherited;
    private LocalDate registrationDate;
    private LocalDate insuranceExpiry;
    private LocalDate inspectionExpiry;
    private String productionDate;
    private Integer keyCount;
    private String description;
    private String title;
    private List<String> images;
    private CarInspectionDTO inspection;
    private List<String> exportCountries;
}
