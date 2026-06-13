package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 品牌实体 — 直接复用 mobile 端 brands 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("brands")
public class AdminCarBrand {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    @TableField("logo_url")
    private String logoUrl;

    private String firstLetter;

    @TableField("sort_order")
    private Integer sortOrder;

    /** ACTIVE / HIDDEN */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
