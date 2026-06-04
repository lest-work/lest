package com.lest.common.security.annotation;

import java.lang.annotation.*;

/**
 * 项目级角色校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireProjectRole
{
    /** 允许访问的最低角色，默认为 MEMBER */
    String value() default "MEMBER";

    /** 是否检查成员身份 */
    boolean checkMembership() default false;

    /** 允许访问的角色集合 */
    String[] roles() default {};
}
