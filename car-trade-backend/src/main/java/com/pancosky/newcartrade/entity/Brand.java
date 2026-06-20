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
 * 车辆品牌字典实体类
 * 描述：车辆品牌静态字典（如"宝马""奔驰""丰田"），供车源、搜索、筛选引用。
 * 关联：作为 car_sources.brandId 的外键目标；一对多关联 series 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_brands")
public class Brand {

    /** 品牌ID（主键，整型，便于前端做硬编码映射） */
    @TableId
    private Integer id;

    /** 品牌名称（中文名称，如"宝马"） */
    private String name;

    /** 品牌LOGO图片URL（用于品牌筛选/列表页展示） */
    private String logoUrl;

    /** 首字母（按 A-Z 拼音首字母，便于前端做字母索引） */
    private String firstLetter;

    /** 排序权重（数字越小越靠前） */
    private Integer sortOrder;

    /** 状态（ACTIVE=启用；HIDDEN=隐藏） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
