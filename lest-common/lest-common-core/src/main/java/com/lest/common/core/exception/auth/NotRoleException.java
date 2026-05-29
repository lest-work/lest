package com.lest.common.core.exception.auth;

/**
 * 角色不足异常
 */
public class NotRoleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotRoleException(String role) {
        super("没有访问角色：" + role);
    }

    public NotRoleException(String[] roles) {
        super("没有访问角色：" + String.join(",", roles));
    }
}
