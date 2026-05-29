package com.lest.common.datasource.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;

/**
 * 数据源切换切面（用于标记方法执行使用哪个数据源）
 *
 * @author yshan2028
 */
@Slf4j
@Aspect
public class DataSourceAspect {

    @Around("execution(* com.lest..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return point.proceed();
    }
}
