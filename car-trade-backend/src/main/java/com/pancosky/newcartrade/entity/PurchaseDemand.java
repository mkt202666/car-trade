package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 求购意向表
 */
@Data
@TableName("tc_purchase_demands")
public class PurchaseDemand {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
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
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
