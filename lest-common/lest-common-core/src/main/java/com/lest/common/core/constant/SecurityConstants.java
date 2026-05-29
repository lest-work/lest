package com.lest.common.core.constant;

/**
 * 服务间调用和安全相关的常量
 *
 * @author yshan2028
 */
public class SecurityConstants {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String FROM_SOURCE = "from-source";
    public static final String INNER = "inner";
    public static final String USER_KEY = "user_key";
    public static final String LOGIN_USER = "login_user";
    public static final String DETAILS_USER_ID = "user_id";
    public static final String DETAILS_USERNAME = "username";
    public static final String ROLE_PERMISSION_DELIMITER = ":";
    public static final String PERMISSION_DELIMITER = ",";
    public static final String DEFAULT_ROLE_KEY = "role_default";
    public static final String DEFAULT_ROLE = "common_role";
    public static final int REGISTER_CODE_EXPIRATION_MINUTES = 10;
}
