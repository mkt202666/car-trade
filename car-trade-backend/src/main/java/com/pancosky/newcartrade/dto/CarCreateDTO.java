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
    private String vin;  // 车架号
    private Integer year;
    private Integer mileage;
    private BigDecimal price;
    private String pricingType;  // FIXED:一口价, AUCTION:拍卖
    private BigDecimal startingPrice;  // 起拍价
    private BigDecimal ceilingPrice;  // 封顶价
    private BigDecimal bidIncrement;  // 加价幅度
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
    private String inspectionReportType;  // LINK:链接, FILE:文件
    private String inspectionReportUrl;  // 检测报告链接/文件URL
    private String certificateMaterials;  // 证件材料JSON
    private Boolean supportLockNegotiation;  // 支持锁车洽谈
    private Boolean aiAutoPromote;  // AI自动推广
    private Boolean isDraft;  // 是否草稿
    private List<String> images;
    private CarInspectionDTO inspection;
    private List<String> exportCountries;
}
