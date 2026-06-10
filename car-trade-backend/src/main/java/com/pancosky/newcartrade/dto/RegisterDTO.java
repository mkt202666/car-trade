package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String phone;
    private String smsCode;
    private String password;
    private String nickname;
}
