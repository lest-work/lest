package com.lest.common.core.constant;

/**
 * 用户相关常量
 *
 * @author yshan2028
 */
public class UserConstants {
    public static final String NORMAL = "0";
    public static final String DISABLE = "1";
    public static final String ROLE_NORMAL = "0";
    public static final String ROLE_DISABLE = "1";
    public static final String DEPT_NORMAL = "0";
    public static final String DEPT_DISABLE = "1";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String TYPE_DIR = "D";
    public static final String TYPE_MENU = "M";
    public static final String TYPE_BUTTON = "B";
    public static final String LAYOUT = "Layout";
    public static final String PARENT_VIEW = "ParentView";
    public static final String INNER_LINK = "I";
    public static final Long SUPER_ADMIN = 1L;
    public static final String SUPER_ADMIN_ROLE = "admin";
    public static final String ROLE_GUEST = "guest";
    public static final String UNIQUE = "0";
    public static final String NOT_UNIQUE = "1";
    public static final String[] DEFAULT_ROLE_KEYS = {"admin", "common"};
    public static final String ROLE_CAPTCHA = "captcha";

    /** 用户名最小长度 */
    public static final int USERNAME_MIN_LENGTH = 2;
    /** 用户名最大长度 */
    public static final int USERNAME_MAX_LENGTH = 20;
    /** 密码最小长度 */
    public static final int PASSWORD_MIN_LENGTH = 5;
    /** 密码最大长度 */
    public static final int PASSWORD_MAX_LENGTH = 20;

    public static boolean isAdmin(Long userId) {
        return userId != null && SUPER_ADMIN.equals(userId);
    }

    public static boolean isAdmin(String userId) {
        if (userId == null || "0".equals(userId)) {
            return false;
        }
        return SUPER_ADMIN.toString().equals(userId) || SUPER_ADMIN_ROLE.equals(userId);
    }
}
