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
