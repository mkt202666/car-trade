package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("car_sources")
public class CarSource {
    @TableId
    private Long id;
    private Long userId;
    private Integer brandId;
    private Integer seriesId;
    private Integer modelId;
    private String title;
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
    private String transmission;
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
    private String inspectionReportType;  // LINK:链接, FILE:文件
    private String inspectionReportUrl;  // 检测报告链接/文件URL
    private String certificateMaterials;  // 证件材料JSON
    private Boolean supportLockNegotiation;  // 支持锁车洽谈
    private Boolean aiAutoPromote;  // AI自动推广
    private Boolean isDraft;  // 是否草稿
    private String auctionStatus;
    private LocalDateTime auctionEndTime;
    private Long viewCount;
    private Integer favoriteCount;
    private String status;
    private LocalDateTime publishedAt;
    private String exportCountries;

    // ============ 非持久化字段（Service 层拼装使用） ============
    @TableField(exist = false)
    private String brandName;
    @TableField(exist = false)
    private String seriesName;
    @TableField(exist = false)
    private String modelName;
    @TableField(exist = false)
    private String coverImage;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
