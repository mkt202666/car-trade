package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {
    private Long id;
    private String nickname;
    private String realName;
    private String phone;
    private String avatarUrl;
    private String shopName;
    private String creditGrade;
    private Integer creditScore;
    private String certificationStatus;
    private String userRole;
    private String status;
    private Integer dealCount;
    private Integer onSaleCount;
    private LocalDateTime createdAt;

    /** 统一社会信用代码 */
    private String creditCode;
    /** 所在省 */
    private String province;
    /** 所在市 */
    private String city;
    /** 身份证号码 */
    private String idCardNumber;
    /** 营业执照图片URL */
    private String businessLicenseUrl;
    /** 身份证正面图片URL */
    private String idCardFrontUrl;
    /** 身份证反面图片URL */
    private String idCardBackUrl;

    // M1 新增字段
    /** 会员到期时间 */
    private LocalDateTime memberExpireAt;
    /** 连续登录失败次数 */
    private Integer loginFailCount;
    /** 账户锁定到期时间 */
    private LocalDateTime lockedUntil;
    /** 通知订阅设置 JSON */
    private String notificationSettings;
}
