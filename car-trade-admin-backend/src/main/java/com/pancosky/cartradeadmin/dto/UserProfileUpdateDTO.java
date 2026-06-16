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
    private String creditCode;
    private String province;
    private String city;
    private String idCardNumber;
    private String businessLicenseUrl;
    private String idCardFrontUrl;
    private String idCardBackUrl;
}
