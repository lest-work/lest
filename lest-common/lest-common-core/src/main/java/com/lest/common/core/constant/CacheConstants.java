package com.lest.common.core.constant;

/**
 * 缓存相关的 Key 常量
 *
 * @author yshan2028
 */
public class CacheConstants {
    /** 登录令牌Key前缀 */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";
    /** 登录令牌有效期（分钟） */
    public static final long EXPIRATION = 720L;
    /** 登录令牌自动刷新阈值（分钟） */
    public static final long REFRESH_TIME = 120L;
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
    public static final String REFRESH_TOKEN_KEY = "refresh_tokens:";

    /** 密码最大错误次数 */
    public static final int PASSWORD_MAX_RETRY_COUNT = 5;
    /** 密码锁定时间（分钟） */
    public static final int PASSWORD_LOCK_TIME = 10;
    public static final String LOGIN_USER_KEY = "login_user_key:";
    public static final String ONLINE_USER_KEY = "online_user:";
    public static final String REGISTER_CODE_KEY = "register_codes:";
    public static final String USER_TOKEN_SCOPE = "user_token_scope:";
    public static final String OPTIONS_DATA_KEY = "options_data:";
    public static final String DICT_DATA_KEY = "sys_dict_data:";
    public static final String DICT_TYPE_KEY = "sys_dict_type:";
    public static final String CONFIG_KEY = "sys_config:";
    public static final String USER_ROLE_KEY = "user_role:";
    public static final String ROLE_MENU_KEY = "role_menu:";
    public static final String ROLE_DEPT_KEY = "role_dept:";
    public static final String USER_DEPT_KEY = "user_dept:";
    public static final String ROUTES_KEY = "routes:";
    public static final String AREAS_KEY = "areas:";
    public static final String PERMISSIONS_KEY = "permissions:";

    /** 登录账户密码错误次数缓存Key */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
    /** 登录IP黑名单缓存Key */
    public static final String SYS_LOGIN_BLACKIPLIST = CONFIG_KEY + "sys.login.blackIPList";
}
