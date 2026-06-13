package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("users")
public class AppUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String password;

    private String nickname;

    private String realName;

    private String avatarUrl;

    private String shopName;

    private String shopLogo;

    private String shopDescription;

    private String creditGrade;

    private Integer creditScore;

    private Integer dealCount;

    private Integer onSaleCount;

    private Long viewCount;

    /** 今日被浏览次数 */
    private Integer viewCountToday;

    /** 累计消息数 */
    private Long messageCount;

    /** 今日消息数 */
    private Integer messageCountToday;

    private Integer followerCount;

    /** 今日新增粉丝数 */
    private Integer followerCountToday;

    /** 会员到期时间 */
    private LocalDateTime memberExpireAt;

    /** 用户角色: PERSONAL-个人用户, SHOP-车行用户, ADMIN-系统管理员, DEVELOPER-开发人员 */
    private String userRole;

    /** NONE / PENDING / CERTIFIED / REJECTED */
    private String certificationStatus;

    /** ACTIVE / FROZEN / DELETED */
    private String status;

    /** 连续登录失败次数 */
    private Integer loginFailCount;

    /** 账户锁定到期时间 */
    private LocalDateTime lockedUntil;

    /** 通知订阅设置 JSON */
    private String notificationSettings;

    /** 非持久化字段：用户简介 */
    @TableField(exist = false)
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
