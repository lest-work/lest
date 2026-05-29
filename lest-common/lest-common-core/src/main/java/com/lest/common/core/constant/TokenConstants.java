package com.lest.common.core.constant;

/**
 * Token 相关的常量
 *
 * @author yshan2028
 */
public class TokenConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String PREFIX = "Bearer ";
    public static final String SECRET = "LestPlatformSecretKeyForJwtTokenGenerationAndValidation2024MustBeLongEnough";
    public static final long MILLIS_SECOND = 1000L;
    public static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    public static final long EXPIRATION_TIME_MINUTES = 720L;
    public static final long REFRESH_EXPIRATION_TIME_MINUTES = 20160L;
    public static final long EXPIRATION = EXPIRATION_TIME_MINUTES * MILLIS_MINUTE;
    public static final long REFRESH_EXPIRATION = REFRESH_EXPIRATION_TIME_MINUTES * MILLIS_MINUTE;
}
