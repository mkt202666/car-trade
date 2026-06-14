package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 出口地区配置实体
 * 描述：管理可出口的国家/地区及其约束条件和要求
 */
@Data
@TableName("export_regions")
public class ExportRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 国家/地区名称（如"俄罗斯"） */
    private String name;

    /** 国家代码（ISO 3166-1 alpha-2，如"RU"） */
    private String code;

    /** 国旗emoji（如"🇷🇺"） */
    private String flag;

    /** 所属地区分组（如"独联体"、"非洲"、"中东"） */
    private String regionGroup;

    /** 参数约束条件（JSON数组，如[{"field":"左舵/右舵","op":"==","value":"左舵"}]） */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String constraints;

    /** 出口要求描述（多行文本） */
    private String requirements;

    /** 状态：ACTIVE/INACTIVE */
    private String status;

    /** 排序号（数字越小越靠前） */
    private Integer sortOrder;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除时间（NULL=未删除） */
    private LocalDateTime deletedAt;
}
