package com.lest.common.core.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Bean 拷贝工具类
 *
 * @author yshan2028
 */
public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    /**
     * 属性拷贝（仅拷贝非 null 属性）
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 属性拷贝（包含 null 属性）
     */
    public static void copyPropertiesIncludeNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * 属性拷贝并返回目标对象
     */
    public static <T> T copyBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed: " + e.getMessage(), e);
        }
    }

    /**
     * 获取对象中为 null 的属性名数组
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrapper = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds;
        try {
            pds = java.beans.Introspector.getBeanInfo(source.getClass())
                    .getPropertyDescriptors();
        } catch (Exception e) {
            return new String[0];
        }
        Set<String> nullNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null && wrapper.getPropertyValue(pd.getName()) == null) {
                nullNames.add(pd.getName());
            }
        }
        return nullNames.toArray(new String[0]);
    }
}
