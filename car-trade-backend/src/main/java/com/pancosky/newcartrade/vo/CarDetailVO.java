package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarDetailVO {
    private Long id;
    private String title;
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer year;
    private Integer mileage;
    private BigDecimal price;
    private BigDecimal deposit;
    private String color;
    private String city;
    private String cityCode;
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
    private String coverImage;
    private List<String> images;
    private List<String> tags;
    private String auctionStatus;
    private LocalDateTime auctionEndTime;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Integer favoriteCount;

    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private String sellerShopName;
    private String sellerCreditGrade;
    private Integer sellerDealCount;
    private Integer sellerFollowerCount;
    private boolean followedByCurrentUser;
    private boolean favoritedByCurrentUser;

    private String overallCondition;
    private String paint;
    private String structure;
    private String engine;
    private String transmission;
    private Integer transferCount;
    private String mileageType;
    private List<String> abnormalPhotos;
}
