package com.lest.project.enums;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * 项目角色类型枚举。
 *
 * <p>对外暴露统一的角色字面值，供权限注解、数据库与接口复用。</p>
 */
public enum ProjectRole {
    ADMIN("admin"),
    MEMBER("member"),
    VIEWER("viewer");

    private final String value;

    ProjectRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字面值寻找对应的枚举，大小写不敏感。
     *
     * @param rawValue 角色字面值
     * @return 匹配到的枚举 Optional
     */
    public static Optional<ProjectRole> fromValue(String rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }
        String stored = normalizeToStoredValue(rawValue);
        if (stored == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
            .filter(role -> role.value.equals(stored))
            .findFirst();
    }

    /**
     * 校验字面值是否属于受支持的角色集合。
     *
     * @param rawValue 角色字面值
     * @return true 表示合法
     */
    public static boolean isValid(String rawValue) {
        return normalizeToStoredValue(rawValue) != null;
    }

    /**
     * 将各种输入形式的角色（如 ADMIN/member/developer/lead/viewer）
     * 归一化为内部存储用的小写字符串。
     */
    public static String normalizeToStoredValue(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.toLowerCase(Locale.ROOT);
        // 直接匹配已有存储值
        if (normalized.equals(ADMIN.value)
            || normalized.equals(MEMBER.value)
            || normalized.equals(VIEWER.value)) {
            return normalized;
        }
        // 产品口径别名：ADMIN / MEMBER / VIEWER（大小写不敏感）
        switch (normalized) {
            case "admin":
                return ADMIN.value;
            case "member":
                return MEMBER.value;
            case "viewer":
                return VIEWER.value;
            default:
                return null;
        }
    }

    /**
     * 将内部存储值转换为产品视角角色：ADMIN / MEMBER / VIEWER。
     */
    public static String toProductRole(String storedValue) {
        if (storedValue == null) {
            return null;
        }
        final String normalized = storedValue.toLowerCase(Locale.ROOT);
        if (normalized.equals(ADMIN.value)) {
            return "ADMIN";
        }
        if (normalized.equals(MEMBER.value)) {
            return "MEMBER";
        }
        if (normalized.equals(VIEWER.value)) {
            return "VIEWER";
        }
        // 未知值按原样返回，便于排查
        return storedValue;
    }
}
