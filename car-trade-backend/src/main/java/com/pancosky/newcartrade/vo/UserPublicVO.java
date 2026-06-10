package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公开信息VO
 * 描述：其他用户可查看的用户信息，用于店铺主页、用户详情页等场景，不包含手机号、真实姓名等隐私字段。
 * 用途：用于 /api/v1/users/{id} 等接口返回。
 */
@Data
public class UserPublicVO {

    /** 用户ID */
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 车行名称 */
    private String shopName;

    /** 信用等级（S / A / B / C 等） */
    private String creditGrade;

    /** 累计成交订单数 */
    private Integer dealCount;

    /** 在售车源数量 */
    private Integer onSaleCount;

    /** 累计粉丝数 */
    private Integer followerCount;

    /** 最近在线时间（用于展示"在线/离线/刚刚活跃"状态） */
    private LocalDateTime lastOnlineAt;
}
