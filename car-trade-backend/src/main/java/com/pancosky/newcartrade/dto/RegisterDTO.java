package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 用户注册请求 DTO
 * 描述：承载新用户注册所需的手机号、短信验证码、密码和昵称。
 * 使用场景：POST /api/auth/register 接口的请求体。
 */
@Data
public class RegisterDTO {

    /** 用户手机号（11位，作为登录账号和系统唯一标识） */
    private String phone;

    /** 短信验证码（6位数字，用于手机号真实性校验） */
    private String smsCode;

    /** 登录密码（注册时设置，后端将加密存储） */
    private String password;

    /** 用户昵称（展示名称，用于订单和消息） */
    private String nickname;
}
