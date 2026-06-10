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
 * 会员套餐方案实体类
 * 描述：平台的会员产品配置（如"普通会员/金牌会员/钻石会员"），供用户购买/续费。
 * 关联：通过 user_membership 表与用户关联实际购买记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("member_plans")
public class MemberPlan {

    /** 套餐ID（主键） */
    @TableId
    private Long id;

    /** 套餐名称（如"金牌会员年卡"） */
    private String name;

    /** 会员等级（BASIC=基础；GOLD=金牌；PLATINUM=白金；DIAMOND=钻石） */
    private String level;

    /** 套餐价格（单位：元/每期） */
    private BigDecimal price;

    /** 有效天数（如 30=月卡，365=年卡） */
    private Integer durationDays;

    /** 套餐权益（JSON字符串，描述会员特权，如{"discountRate":80,"freeInspection":true}） */
    private String benefits;

    /** 排序权重（数字越小越靠前，用于会员购买页排序展示） */
    private Integer sortOrder;

    /** 套餐状态（ACTIVE=可用；HIDDEN=隐藏；ARCHIVED=已下架） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
