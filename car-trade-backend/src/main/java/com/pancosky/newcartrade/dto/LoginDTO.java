package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String phone;
    private String smsCode;
    private String password;
}
