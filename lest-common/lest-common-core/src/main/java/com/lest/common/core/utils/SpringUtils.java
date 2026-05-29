package com.lest.common.core.utils;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 工具类
 *
 * @author yshan2028
 */
@Component
public final class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clz);
    }

    /**
     * 获取Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        if (applicationContext == null) {
            return null;
        }
        return (T) applicationContext.getBean(name);
    }

    /**
     * 获取Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, Class<T> clz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(name, clz);
    }

    /**
     * 判断对象是否为代理对象
     */
    public static boolean isAopProxy(Object object) {
        return object != null && object.getClass().getName().contains("$$");
    }
}
