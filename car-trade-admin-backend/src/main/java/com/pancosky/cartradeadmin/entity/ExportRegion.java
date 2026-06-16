package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("export_regions")
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

    private String constraints;

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
