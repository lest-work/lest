package com.lest.common.log.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Around("@within(org.springframework.stereotype.Service)")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = point.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("{}.{} - {}ms", className, methodName, elapsed);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("{}.{} - {}ms - ERROR: {}", className, methodName, elapsed, e.getMessage());
            throw e;
        }
    }
}
