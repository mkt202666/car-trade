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
@TableName("tc_users")
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

    /** 统一社会信用代码 */
    private String creditCode;

    /** 所在省 / 驻点省份 */
    private String province;

    /** 所在市 / 驻点城市 */
    private String city;

    /** 实体店详细经营地址 */
    private String address;

    /** 身份证号码 */
    private String idCardNumber;

    /** 营业执照图片URL */
    private String businessLicenseUrl;

    /** 营业执照附件 URL (管理后台表单用) */
    private String licenseUrl;

    /** 身份证正面图片URL */
    private String idCardFrontUrl;

    /** 身份证反面图片URL */
    private String idCardBackUrl;

    /** 申请人身份证图片 URL (管理后台表单用) */
    private String idCardImageUrl;

    /** 车行实体门店图片 URL */
    private String storeImageUrl;

    /** 保证金余额（元） */
    private Long depositBalance;

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

    /** ACTIVE / FROZEN / DELETED / SUSPENDED / INACTIVE */
    private String status;

    /** 营业执照号 */
    private String businessLicense;

    /** 最近一次审核拒绝原因 */
    private String rejectReason;

    /** 审核人ID (FK -> admin_users.id) */
    private Long reviewerId;

    /** 最近审核时间 */
    private LocalDateTime reviewedAt;

    /** 连续登录失败次数 */
    private Integer loginFailCount;

    /** 账户锁定到期时间 */
    private LocalDateTime lockedUntil;

    /** 通知订阅设置 JSON */
    @TableField(typeHandler = com.pancosky.cartradeadmin.handler.JsonbStringTypeHandler.class)
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
