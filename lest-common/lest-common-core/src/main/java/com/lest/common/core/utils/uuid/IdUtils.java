package com.lest.common.core.utils.uuid;

/**
 * ID生成器工具类
 *
 * @author yshan2028
 */
public class IdUtils {

    private static final java.util.UUID RANDOM_UUID = java.util.UUID.randomUUID();

    private IdUtils() {
    }

    /**
     * 获取随机UUID
     */
    public static String randomUUID() {
        return RANDOM_UUID.toString();
    }

    /**
     * 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return RANDOM_UUID.toString().replace("-", "");
    }

    /**
     * 获取随机UUID，使用 {@code "-"} 分隔
     */
    public static String uuid() {
        return RANDOM_UUID.toString();
    }

    /**
     * fast UUID生成
     */
    public static String fastUUID() {
        return RANDOM_UUID.toString().replace("-", "");
    }

    /**
     * 快速生成带前缀的UUID
     */
    public static String fastSimpleUUID() {
        return fastUUID();
    }
}
