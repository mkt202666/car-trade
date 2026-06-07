package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String nickname;
    private String realName;
    private String phone;
    private String avatar;
    private String shopName;
    private String creditGrade;
    private Integer creditScore;
    private Integer dealCount;
    private LocalDateTime memberExpireAt;
    private BigDecimal depositBalance;
    private BigDecimal depositTotal;
    private Integer onSaleCount;
    private Long viewCount;
    private Integer viewCountToday;
    private Long messageCount;
    private Integer messageCountToday;
    private Integer followerCount;
    private Integer followerCountToday;
    private String certificationStatus;
    private String description;
    private LocalDateTime createdAt;
}
