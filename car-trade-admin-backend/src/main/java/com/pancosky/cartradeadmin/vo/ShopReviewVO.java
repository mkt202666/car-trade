package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopReviewVO {
    private Long id;
    private String shopName;
    private String realName;
    private String phone;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private String certificationStatus;
}
