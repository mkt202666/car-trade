package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户会员记录VO
 * 描述：用户已购买的会员记录，展示套餐名称、等级、有效期、状态等。
 */
@Data
public class UserMembershipVO {

    /** 会员记录ID */
    private Long id;

    /** 套餐名称 */
    private String planName;

    /** 会员等级 */
    private String level;

    /** 会员生效起始时间 */
    private LocalDateTime startAt;

    /** 会员到期时间 */
    private LocalDateTime expireAt;

    /** 会员状态（ACTIVE 有效 / EXPIRED 已过期 / FROZEN 已冻结） */
    private String status;

    /** 关联套餐ID */
    private Long planId;

    /** 关联用户ID */
    private Long userId;

    /** 会员结束时间（实际结束时间，含提前冻结/到期） */
    private LocalDateTime endAt;
}
