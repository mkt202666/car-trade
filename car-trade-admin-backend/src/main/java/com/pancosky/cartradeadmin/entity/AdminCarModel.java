package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车型实体 — 直接复用 mobile 端 models 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_models")
public class AdminCarModel {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("series_id")
    private Integer seriesId;

    private String name;

    private Integer year;

    @TableField("guide_price")
    private BigDecimal guidePrice;

    @TableField("sort_order")
    private Integer sortOrder;

    /** ACTIVE / HIDDEN */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
