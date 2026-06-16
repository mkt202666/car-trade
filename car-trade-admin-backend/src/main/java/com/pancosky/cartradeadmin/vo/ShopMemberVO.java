package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopMemberVO {
    private Long id;
    private Long memberUserId;
    private String nickname;
    private String realName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;
    private LocalDateTime joinedAt;
}
