package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("car_inspections")
public class AppCarInspection {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long carId;

    @TableField("overall_condition")
    private String overallCondition;

    @TableField("paint")
    private String paint;

    @TableField("structure")
    private String structure;

    @TableField("engine")
    private String engine;

    @TableField("transmission")
    private String transmission;

    @TableField("transfer_count")
    private Integer transferCount;

    @TableField("mileage_type")
    private String mileageType;

    @TableField("description")
    private String description;

    /** JSONB column stored as String */
    @TableField("abnormal_photos")
    private String abnormalPhotos;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
