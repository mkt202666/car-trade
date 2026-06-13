package com.pancosky.cartradeadmin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAdmin {

    /**
     * 需要的角色，空数组表示不需要特定角色（只要有登录即可）
     */
    String[] value() default {};
}
