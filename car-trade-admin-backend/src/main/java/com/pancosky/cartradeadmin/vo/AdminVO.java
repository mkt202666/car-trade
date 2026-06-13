package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String role;

    private String permissions;

    private String status;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;
}
