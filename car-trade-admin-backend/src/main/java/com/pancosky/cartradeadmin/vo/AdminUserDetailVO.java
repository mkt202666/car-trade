package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminUserDetailVO {
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
    private Long viewCount;
    private Integer followerCount;
    private String shopDescription;
    private LocalDateTime memberExpireAt;
    private String notificationSettings;
    private BigDecimal depositBalance;
}
