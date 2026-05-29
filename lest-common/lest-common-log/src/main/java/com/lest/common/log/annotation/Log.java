package com.lest.common.log.annotation;

import com.lest.common.log.enums.BusinessType;
import com.lest.common.log.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义日志记录注解
 *
 * @author yshan2028
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 操作标题
     */
    String title() default "";

    /**
     * 业务操作类型
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人员类型
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求数据
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应数据
     */
    boolean isSaveResponseData() default true;
}
