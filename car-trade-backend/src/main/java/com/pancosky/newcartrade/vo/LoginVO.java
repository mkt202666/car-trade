package com.pancosky.newcartrade.vo;

import lombok.Data;

/**
 * 登录响应VO
 * 描述：用户登录/注册成功后返回的令牌与用户基本信息。前端将 accessToken 写入请求头，refreshToken 用于静默续期。
 * 用途：用于 /api/v1/users/login 与 /api/v1/users/register 接口返回。
 */
@Data
public class LoginVO {

    /** 访问令牌（JWT 格式，需要放入 HTTP 请求头 Authorization: Bearer &lt;accessToken&gt;） */
    private String accessToken;

    /** 刷新令牌（用于 accessToken 过期后换取新的访问令牌） */
    private String refreshToken;

    /** 访问令牌有效期（秒，一般为 7200 秒） */
    private Long expiresIn;

    /** 当前登录用户的完整个人信息 */
    private UserVO user;
}
