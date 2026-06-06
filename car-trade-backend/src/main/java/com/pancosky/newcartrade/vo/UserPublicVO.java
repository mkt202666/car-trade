package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPublicVO {
    private Long id;
    private String nickname;
    private String avatar;
    private String shopName;
    private String creditGrade;
    private Integer dealCount;
    private Integer onSaleCount;
    private Integer followerCount;
    private LocalDateTime lastOnlineAt;
}
