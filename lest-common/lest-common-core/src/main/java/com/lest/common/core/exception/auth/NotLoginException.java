package com.lest.common.core.exception.auth;

/**
 * 认证异常
 */
public class NotLoginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotLoginException(String message) {
        super(message);
    }
}
