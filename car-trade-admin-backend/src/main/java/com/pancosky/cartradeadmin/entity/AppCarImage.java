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
@TableName("car_images")
public class AppCarImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long carId;

    @TableField("image_url")
    private String imageUrl;

    @TableField("image_type")
    private String imageType;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
