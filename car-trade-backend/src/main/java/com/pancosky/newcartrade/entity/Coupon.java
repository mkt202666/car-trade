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
 * 优惠券/活动券实体类
 * 描述：平台发行的折扣券/满减券，用户可领取并在下单时使用以抵扣金额。
 * 关联：通过 user_coupons 表与 users 关联（用户-优惠券领取关系）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("coupons")
public class Coupon {

    /** 优惠券ID（主键） */
    @TableId
    private Long id;

    /** 优惠券名称（如"满5万减500元"） */
    private String name;

    /** 优惠类型（FIXED_DISCOUNT=固定金额抵扣；PERCENT_DISCOUNT=比例折扣；FREE_SHIPPING=免交易服务费） */
    private String type;

    /** 优惠值（金额或比例。FIXED 单位元；PERCENT 单位百分比 0-100） */
    private BigDecimal value;

    /** 最低使用门槛（订单总金额需达到此值后方可使用，单位：元） */
    private BigDecimal minAmount;

    /** 发行总数量（活动总库存） */
    private Integer totalCount;

    /** 剩余数量（当前未被领取的数量，用于限流） */
    private Integer remainCount;

    /** 优惠券生效开始时间（提前配置，按时间自动生效） */
    private LocalDateTime startAt;

    /** 优惠券过期时间（超过该时间后不可领取、不可使用） */
    private LocalDateTime endAt;

    /** 优惠券状态（ACTIVE=发行中；PAUSED=暂停；EXPIRED=已过期；DEPLETED=已领完） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
