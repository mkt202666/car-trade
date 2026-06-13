package com.pancosky.cartradeadmin.annotation;

import java.lang.annotation.*;

/**
 * Audit log annotation for tracking admin operations.
 * Applied to controller methods that modify data (create, update, delete).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /** Module name, e.g. "auth", "user", "shop", "car" */
    String module();

    /** Operation description, e.g. "管理员登录", "修改用户状态" */
    String action();

    /** Target entity type, e.g. "user", "shop", "car_source" */
    String targetType() default "";

    /** Whether to record request parameters (default true) */
    boolean recordParams() default true;
}
