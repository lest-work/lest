package com.lest.common.core;

/**
 * 业务异常
 *
 * @author yshan2028
 * @deprecated use {@link com.lest.common.core.exception.ServiceException}
 */
@Deprecated
public class BusinessException extends com.lest.common.core.exception.ServiceException {

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(int code, String message, Object detailMessage) {
        super(code, message, detailMessage);
    }
}
