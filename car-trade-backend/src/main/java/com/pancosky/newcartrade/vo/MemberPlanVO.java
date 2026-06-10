package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员套餐VO
 * 描述：会员套餐列表项，用于开通/续费会员页面展示套餐信息。
 */
@Data
public class MemberPlanVO {

    /** 套餐ID */
    private Long id;

    /** 套餐名称（如"高级会员"） */
    private String name;

    /** 会员等级（如 BASIC / ADVANCED / PREMIUM） */
    private String level;

    /** 套餐价格（元） */
    private BigDecimal price;

    /** 套餐有效天数（如 30 / 90 / 365） */
    private Integer durationDays;

    /** 套餐权益描述（文本或 JSON 说明） */
    private String benefits;
}
