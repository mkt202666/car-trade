package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopDetailVO {
    private Long id;
    private String shopName;
    private String shopLogo;
    private String shopDescription;
    private String realName;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String creditGrade;
    private Integer creditScore;
    private Integer dealCount;
    private Integer onSaleCount;
    private Long viewCount;
    private Integer followerCount;
    private String certificationStatus;
    private String status;
    private LocalDateTime createdAt;
    private List<ShopMemberVO> members;
}
