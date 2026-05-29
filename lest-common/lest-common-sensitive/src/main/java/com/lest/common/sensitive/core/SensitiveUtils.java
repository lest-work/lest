package com.lest.common.sensitive.core;

import com.lest.common.sensitive.annotation.Sensitive;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 数据脱敏工具类
 *
 * @author yshan2028
 */
@Component
public class SensitiveUtils {

    /**
     * 对对象中标注了 @Sensitive 的字段进行脱敏
     *
     * @param target 要脱敏的对象
     * @param <T>    对象类型
     * @return 脱敏后的对象（返回新对象，原对象不变）
     */
    public static <T> T desensitize(T target) {
        if (target == null) {
            return null;
        }
        try {
            T result = (T) target.getClass().getDeclaredConstructor().newInstance();
            for (Field field : target.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(target);
                Sensitive sensitive = field.getAnnotation(Sensitive.class);
                if (sensitive != null && value instanceof String strValue) {
                    String desensitized = sensitive.strategy().desensitize(strValue);
                    field.set(result, desensitized);
                } else {
                    field.set(result, value);
                }
            }
            return result;
        } catch (Exception e) {
            return target;
        }
    }

    /**
     * 对单个字符串进行脱敏
     *
     * @param value   原始值
     * @param strategy 脱敏策略
     * @return 脱敏后的值
     */
    public static String desensitize(String value, SensitiveStrategy strategy) {
        return strategy.desensitize(value);
    }
}
