package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopVO {
    private Long id;
    private String shopName;
    private String realName;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String creditGrade;
    private Integer creditScore;
    private Integer dealCount;
    private Integer onSaleCount;
    private Integer memberCount;
    private String status;
    private String certificationStatus;
    private LocalDateTime createdAt;
}
