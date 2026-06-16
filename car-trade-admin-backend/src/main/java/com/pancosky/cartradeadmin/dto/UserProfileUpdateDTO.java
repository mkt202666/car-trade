package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String nickname;
    private String realName;
    private String avatarUrl;
    private String shopName;
    private String shopLogo;
    private String shopDescription;
}
