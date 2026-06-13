package com.pancosky.newcartrade.common;

/**
 * 接口权限等级
 * 说明：
 *   PUBLIC    公开接口，任何人都可以调用，不校验 token。
 *   PROTECTED 需要登录的接口，必须携带有效的 token 才能调用。
 *   CERTIFIED 需要商家认证的接口，必须是 CERTIFIED 状态的用户。
 *   ADMIN     管理员接口，用于平台管理侧，预留字段。
 * 用法：
 *   在 Controller 方法上添加 @RequiresAuth(AuthLevel.PROTECTED)，
 *   拦截器会根据级别进行相应的认证、权限检查。
 */
public enum AuthLevel {
    PUBLIC,
    PROTECTED,
    CERTIFIED,
    ADMIN
}
