package com.lest.common.sensitive.annotation;

import com.lest.common.sensitive.core.SensitiveStrategy;

import java.lang.annotation.*;

/**
 * 字段脱敏注解，标注在实体类字段上
 *
 * @author yshan2028
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    /**
     * 脱敏策略
     */
    SensitiveStrategy strategy();
}
