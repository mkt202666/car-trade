package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMembershipVO {
    private Long id;
    private String planName;
    private String level;
    private LocalDateTime startAt;
    private LocalDateTime expireAt;
    private String status;
}
