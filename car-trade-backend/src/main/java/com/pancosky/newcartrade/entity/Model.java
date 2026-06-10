package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车型字典实体类
 * 描述：具体车型配置（如"宝马5系 2023款 525Li 豪华套装"），包含新车指导价等。
 * 关联：外键 seriesId → series；被 car_sources.modelId 引用。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("models")
public class Model {

    /** 车型ID（主键） */
    @TableId
    private Integer id;

    /** 所属车系ID（关联 series.id） */
    private Integer seriesId;

    /** 车型名称（如"525Li 豪华套装"） */
    private String name;

    /** 出厂年份（如 2023） */
    private Integer year;

    /** 厂商指导价（单位：元，用于对比车源报价） */
    private BigDecimal guidePrice;

    /** 排序权重（数字越小越靠前） */
    private Integer sortOrder;

    /** 状态（ACTIVE=启用；HIDDEN=隐藏） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
