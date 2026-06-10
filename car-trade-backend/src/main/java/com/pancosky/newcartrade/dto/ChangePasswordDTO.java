package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 修改登录密码请求 DTO
 * 描述：用户在"我的-账号安全"中修改密码时的请求体。
 * 使用场景：PUT /api/users/me/password 接口的请求体。
 */
@Data
public class ChangePasswordDTO {

    /** 原密码（用于验证当前用户身份） */
    private String oldPassword;

    /** 新密码（替换原密码；加密后存储） */
    private String newPassword;
}
