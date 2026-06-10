package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券VO
 * 描述：优惠券卡片展示信息，用于领券中心、优惠券活动页等场景。
 */
@Data
public class CouponVO {

    /** 优惠券ID */
    private Long id;

    /** 优惠券名称（如"新用户专享券"） */
    private String name;

    /** 优惠券类型（如 CASH 现金券 / DISCOUNT 折扣券 / FREE_SHIPPING 包邮券） */
    private String type;

    /** 面值（或折扣值，依 type 解释） */
    private BigDecimal value;

    /** 最低使用金额（订单金额必须大于该值才能使用） */
    private BigDecimal minAmount;

    /** 有效期截止时间（过期后不可用） */
    private LocalDateTime endAt;
}
