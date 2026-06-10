package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 * 描述：用户已领取/已使用的优惠券列表项，包含用户的使用状态、有效期等。
 */
@Data
public class UserCouponVO {

    /** 用户优惠券记录ID */
    private Long id;

    /** 优惠券名称 */
    private String couponName;

    /** 优惠券类型 */
    private String type;

    /** 面值 */
    private BigDecimal value;

    /** 最低使用金额 */
    private BigDecimal minAmount;

    /** 使用状态（AVAILABLE 可用 / USED 已用 / EXPIRED 过期） */
    private String status;

    /** 有效期截止时间 */
    private LocalDateTime endAt;

    /** 关联优惠券模板ID */
    private Long couponId;

    /** 关联用户ID */
    private Long userId;

    /** 领取时间 */
    private LocalDateTime receivedAt;

    /** 使用时间（使用后填入） */
    private LocalDateTime usedAt;
}
