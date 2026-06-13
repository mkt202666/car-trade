package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("export_templates")
public class ExportTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String format;

    @TableField("field_mappings")
    private String fieldMappings;

    @TableField("default_filters")
    private String defaultFilters;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
