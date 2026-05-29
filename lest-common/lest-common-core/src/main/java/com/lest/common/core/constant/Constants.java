package com.lest.common.core.constant;

/**
 * 通用的系统级常量
 *
 * @author yshan2028
 */
public class Constants {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    public static final int LOGIN_SUCCESS = 200;
    public static final int LOGOUT = 200;
    public static final int REGISTER = 200;
    public static final int LOGIN_FAIL = 500;
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
    public static final String CAPTCHA_EXPIRATION = "captchaExpiration:";
    public static final String TOKEN = "token";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String LOGIN_USER_KEY = "login_user_key:";
    public static final String EXPIRATION = "expiration";
    public static final String USER_KEY = "user_key";
    public static final int CAPTCHA_EXPIRATION_MINUTES = 2;
    public static final int TOKEN_EXPIRATION_MINUTES = 720;
    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String UNKNOWN = "unknown";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String BASE64_PREFIX = "data:image/jpeg;base64,";
    public static final String ROLE = "role";
    public static final String ROLE_DELIMITER = ",";
    public static final String LAYOUT = "Layout";
    public static final String INNER = "inner";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    /**
     * 部门相关常量
     */
    public static class Dept {
        /** 全部数据权限 */
        public static final String DATA_SCOPE_ALL = "1";
        /** 自定数据权限 */
        public static final String DATA_SCOPE_CUSTOM = "2";
        /** 部门数据权限 */
        public static final String DATA_SCOPE_DEPT = "3";
        /** 部门及以下数据权限 */
        public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";
        /** 仅本人数据权限 */
        public static final String DATA_SCOPE_SELF = "5";
    }
}
