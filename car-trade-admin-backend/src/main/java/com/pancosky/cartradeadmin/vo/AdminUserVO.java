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
