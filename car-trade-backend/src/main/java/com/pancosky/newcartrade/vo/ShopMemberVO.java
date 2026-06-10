package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺成员VO
 * 描述：店铺成员/店员列表项，用于店铺管理展示成员申请、状态等。
 */
@Data
public class ShopMemberVO {

    /** 成员记录ID */
    private Long id;

    /** 成员用户ID（即店员账号） */
    private Long memberUserId;

    /** 成员昵称 */
    private String nickname;

    /** 成员头像URL */
    private String avatar;

    /** 角色（如 OWNER 店主 / MANAGER 管理员 / STAFF 店员） */
    private String role;

    /** 成员状态（APPLYING 申请中 / ACTIVE 已激活 / REMOVED 已移除） */
    private String status;

    /** 申请时间 */
    private LocalDateTime appliedAt;

    /** 店主审核通过时间 */
    private LocalDateTime approvedAt;

    /** 店铺用户ID（即店主的用户ID） */
    private Long shopUserId;
}
