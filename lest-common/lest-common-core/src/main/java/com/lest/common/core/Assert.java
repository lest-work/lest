package com.lest.common.core;

import com.lest.common.core.utils.StringUtils;

/**
 * 断言工具类
 *
 * @author yshan2028
 */
public class Assert {

    public static void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw new com.lest.common.core.exception.ServiceException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    public static void isNull(Object obj, ErrorCode errorCode) {
        if (obj != null) {
            throw new com.lest.common.core.exception.ServiceException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    public static void isTrue(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw new com.lest.common.core.exception.ServiceException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    public static void isFalse(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new com.lest.common.core.exception.ServiceException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    public static void notEmpty(String str, ErrorCode errorCode) {
        if (StringUtils.isEmpty(str)) {
            throw new com.lest.common.core.exception.ServiceException(errorCode.getCode(), errorCode.getMessage());
        }
    }
}
