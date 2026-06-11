package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 当前用户信息VO
 * 描述：登录用户在个人中心、个人资料等场景展示的完整个人信息，包含会员、保证金、浏览、消息、关注等汇总数据。
 * 用途：用于 /api/v1/users/me 等接口返回。
 */
@Data
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 昵称（用户可编辑的展示名） */
    private String nickname;

    /** 真实姓名（实名认证后填入） */
    private String realName;

    /** 手机号（脱敏后展示，如 138****8001） */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 车行名称（若为商家账号则展示） */
    private String shopName;

    /** 车行Logo地址 */
    private String shopLogo;

    /** 车行简介 */
    private String shopDescription;

    /** 信用等级（如 S / A / B / C） */
    private String creditGrade;

    /** 信用分数（0~1000 分） */
    private Integer creditScore;

    /** 累计成交订单数 */
    private Integer dealCount;

    /** 会员过期时间（null 表示非会员） */
    private LocalDateTime memberExpireAt;

    /** 保证金账户余额 */
    private BigDecimal depositBalance;

    /** 保证金累计充值总额 */
    private BigDecimal depositTotal;

    /** 在售车源数量 */
    private Integer onSaleCount;

    /** 累计被浏览次数（用户主页/车源被浏览总数） */
    private Long viewCount;

    /** 今日新增浏览数 */
    private Integer viewCountToday;

    /** 累计消息数（所有渠道消息总量） */
    private Long messageCount;

    /** 今日新增消息数 */
    private Integer messageCountToday;

    /** 累计粉丝数 */
    private Integer followerCount;

    /** 今日新增粉丝数 */
    private Integer followerCountToday;

    /** 实名认证状态（如 NONE / PENDING / CERTIFIED / REJECTED） */
    private String certificationStatus;

    /** 个人简介（用户自主编辑的简介文本） */
    private String description;

    /** 账号注册时间 */
    private LocalDateTime createdAt;
}
