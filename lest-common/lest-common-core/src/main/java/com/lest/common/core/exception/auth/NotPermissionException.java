package com.lest.common.core.exception.auth;

/**
 * 权限不足异常
 */
public class NotPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotPermissionException(String permission) {
        super("没有访问权限：" + permission);
    }

    public NotPermissionException(String[] permissions) {
        super("没有访问权限：" + String.join(",", permissions));
    }
}
