package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 车系实体 — 直接复用 mobile 端 series 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_series")
public class AdminCarSeries {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("brand_id")
    private Integer brandId;

    private String name;

    @TableField("sort_order")
    private Integer sortOrder;

    /** ACTIVE / HIDDEN */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
