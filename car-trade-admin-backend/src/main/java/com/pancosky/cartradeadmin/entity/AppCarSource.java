package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("car_sources")
public class AppCarSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer brandId;

    private Integer seriesId;

    private Integer modelId;

    private String title;

    private String cityName;

    private String energyType;

    private BigDecimal price;

    private Integer mileage;

    private Integer year;

    /** ON_SALE / SOLD / OFFLINE / DRAFT */
    private String status;

    private Long viewCount;

    private Integer favoriteCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
