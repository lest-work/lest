package com.lest.common.core.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程安全的用户上下文持有者
 *
 * @author yshan2028
 */
public class SecurityContextHolder {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_KEY = "userKey";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PERMISSION = "permission";

    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static void remove() {
        CONTEXT.remove();
    }

    public static Long getUserId() {
        Object value = CONTEXT.get().get(KEY_USER_ID);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return Long.parseLong(value.toString());
    }

    public static void setUserId(Long userId) {
        CONTEXT.get().put(KEY_USER_ID, userId);
    }

    public static String getUserName() {
        return (String) CONTEXT.get().get(KEY_USERNAME);
    }

    public static void setUserName(String username) {
        CONTEXT.get().put(KEY_USERNAME, username);
    }

    public static String getPermission() {
        return (String) CONTEXT.get().get(KEY_PERMISSION);
    }

    public static void setPermission(String permission) {
        CONTEXT.get().put(KEY_PERMISSION, permission);
    }

    public static String getUserKey() {
        return (String) CONTEXT.get().get(KEY_USER_KEY);
    }

    public static void setUserKey(String userKey) {
        CONTEXT.get().put(KEY_USER_KEY, userKey);
    }

    public static String getToken() {
        return (String) CONTEXT.get().get(KEY_TOKEN);
    }

    public static void setToken(String token) {
        CONTEXT.get().put(KEY_TOKEN, token);
    }

    public static Map<String, Object> getContext() {
        return CONTEXT.get();
    }

    public static void setContext(Map<String, Object> context) {
        CONTEXT.get().putAll(context);
    }
}
