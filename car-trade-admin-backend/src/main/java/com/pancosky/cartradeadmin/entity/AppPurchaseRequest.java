package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 求购意向 — 复用 mobile 端 purchase_demands 表
 * 运营端管理匹配时使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("purchase_demands")
public class AppPurchaseRequest {

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

    /** ACTIVE / CANCELLED / FULFILLED / MATCHED / CLOSED */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
