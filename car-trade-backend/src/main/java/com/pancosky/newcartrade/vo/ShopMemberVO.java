package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopMemberVO {
    private Long id;
    private Long memberUserId;
    private String nickname;
    private String avatar;
    private String role;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private Long shopUserId;
}
