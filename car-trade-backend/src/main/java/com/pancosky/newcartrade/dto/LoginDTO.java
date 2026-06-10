package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 用户登录请求 DTO
 * 描述：承载登录时的手机号、短信验证码、登录密码。支持短信验证码登录或密码登录两种方式。
 * 使用场景：POST /api/auth/login 接口的请求体。
 * 注意：登录时 smsCode 与 password 二选一，具体由业务层根据配置选择校验方式。
 */
@Data
public class LoginDTO {

    /** 用户手机号（11位，登录账号） */
    private String phone;

    /** 短信验证码（6位数字，使用短信验证码登录时必填） */
    private String smsCode;

    /** 登录密码（使用密码登录时必填，需配合后端 BCrypt 校验） */
    private String password;
}
