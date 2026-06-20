package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 车源图片实体类
 * 描述：车源的多图集合，支持主图、外观、内饰、细节、检测报告等分类。
 * 关联：外键 carId → car_sources。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_car_images")
public class CarImage {

    /** 图片ID（主键） */
    @TableId
    private Long id;

    /** 所属车源ID（关联 car_sources.id） */
    private Long carId;

    /** 图片URL（可访问的图片地址） */
    private String imageUrl;

    /** 图片类型（COVER=主图；EXTERIOR=外观；INTERIOR=内饰；DETAIL=细节；REPORT=检测报告） */
    private String imageType;

    /** 排序权重（数字越小越靠前，用于控制前端展示顺序） */
    private Integer sortOrder;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
