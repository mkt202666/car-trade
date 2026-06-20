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
 * 车系字典实体类
 * 描述：车辆品牌下的车系（如"宝马5系""宝马3系"），用于车源筛选与详情展示。
 * 关联：外键 brandId → brands；一对多关联 models 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_series")
public class Series {

    /** 车系ID（主键） */
    @TableId
    private Integer id;

    /** 所属品牌ID（关联 brands.id） */
    private Integer brandId;

    /** 车系名称（如"5系"） */
    private String name;

    /** 排序权重（数字越小越靠前） */
    private Integer sortOrder;

    /** 状态（ACTIVE=启用；HIDDEN=隐藏） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
