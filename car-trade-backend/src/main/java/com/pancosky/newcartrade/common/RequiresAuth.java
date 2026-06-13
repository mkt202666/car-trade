package com.pancosky.newcartrade.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限注解
 *
 * 用法：
 *   1. 在 Controller 类上使用：@RequiresAuth(AuthLevel.PROTECTED) —— 该类所有方法默认都要求登录。
 *   2. 在 Controller 方法上使用：@RequiresAuth(AuthLevel.PUBLIC) —— 单个方法覆盖类级别。
 *   3. 不写则默认为：需要拦截器根据 WebMvcConfig 的白名单判断；
 *      如果不在白名单内，则按 AuthLevel.PROTECTED 处理。
 *
 * 优先级：方法级注解 > 类级注解 > WebMvcConfig 白名单。
 *
 * 示例：
 *   @RestController
 *   @RequestMapping("/api/v1/cars")
 *   @RequiresAuth(AuthLevel.PROTECTED)
 *   public class CarController {
 *
 *       @GetMapping
 *       @RequiresAuth(AuthLevel.PUBLIC)  // 列表查看公开，不需要登录
 *       public ApiResponse<?> list() { ... }
 *
 *       @PostMapping                    // 继承类级注解，需要登录
 *       public ApiResponse<?> create() { ... }
 *   }
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuth {
    AuthLevel value() default AuthLevel.PROTECTED;
}
