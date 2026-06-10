package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户统计数据VO
 * 描述：在"我的"页、店铺主页等场景展示的统计信息，包括浏览、消息、关注、成交、信用、会员状态。
 * 用途：用于 /api/v1/users/me/stats 等接口返回。
 */
@Data
public class UserStatsVO {

    /** 在售车源数量 */
    private Integer onSaleCount;

    /** 累计被浏览次数 */
    private Long viewCount;

    /** 今日新增浏览数 */
    private Integer viewCountToday;

    /** 累计消息数 */
    private Long messageCount;

    /** 今日新增消息数 */
    private Integer messageCountToday;

    /** 累计粉丝数 */
    private Integer followerCount;

    /** 今日新增粉丝数 */
    private Integer followerCountToday;

    /** 累计成交订单数 */
    private Integer dealCount;

    /** 信用分数（0~1000） */
    private Integer creditScore;

    /** 会员过期时间（null 表示非会员） */
    private LocalDateTime memberExpireAt;

    /** 实名认证状态（NONE / PENDING / CERTIFIED / REJECTED） */
    private String certificationStatus;
}
