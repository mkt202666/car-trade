package com.pancosky.cartradeadmin.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 基于Redis滑动窗口实现，支持按IP或用户维度限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 时间窗口（秒），默认60秒 */
    int window() default 60;

    /** 时间窗口内最大请求数，默认30次 */
    int maxRequests() default 30;

    /** 限流维度：IP 或 USER，默认按IP限流 */
    LimitType limitType() default LimitType.IP;

    /** 限流提示消息 */
    String message() default "请求过于频繁，请稍后再试";

    /** 限流维度枚举 */
    enum LimitType {
        /** 按客户端IP限流 */
        IP,
        /** 按登录用户ID限流 */
        USER
    }
}
