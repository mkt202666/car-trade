package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

import com.pancosky.cartradeadmin.handler.JsonbStringTypeHandler;

@Data
@TableName(value = "tc_export_regions", autoResultMap = true)
public class ExportRegion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String flag;

    @TableField("region_group")
    private String regionGroup;

    @TableField("group_key")
    private String groupKey;

    private String icon;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String constraints;

    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String requirements;

    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
