package com.lest.modules.auth.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * 错误码规则：
 * - 认证模块: 1000-1999
 * - 用户模块: 2000-2999
 * - 角色模块: 3000-3999
 * - 菜单模块: 4000-4999
 * - 机构模块: 5000-5999
 * - 字典模块: 6000-6999
 * - 通用错误码: 9500-9999
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@AllArgsConstructor
public enum ErrorCode implements com.lest.common.base.ErrorCode {

    // ========== 认证模块 (1000-1999) ==========
    AUTH_USERNAME_PASSWORD_ERROR(1000, "用户名或密码错误"),
    AUTH_CAPTCHA_EXPIRED(1001, "验证码已过期"),
    AUTH_CAPTCHA_ERROR(1002, "验证码错误"),
    AUTH_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    AUTH_TOKEN_EXPIRED(1004, "Access Token 已过期"),
    AUTH_TOKEN_INVALID(1005, "Token 无效或被篡改"),
    AUTH_REFRESH_TOKEN_EXPIRED(1006, "刷新令牌已过期，需重新登录"),
    AUTH_REFRESH_TOKEN_USED(1007, "刷新令牌已被使用，防止重放攻击"),
    AUTH_PASSWORD_SAME_AS_OLD(1008, "新密码不能与当前密码相同"),
    AUTH_PASSWORD_FORMAT_INVALID(1009, "密码格式不正确（长度8-32位，需包含字母和数字）"),
    AUTH_OLD_PASSWORD_ERROR(1010, "当前密码输入错误"),
    AUTH_CAPTCHA_LOCKED(1011, "验证码错误次数过多，请稍后再试"),
    AUTH_CAPTCHA_NOT_FOUND(1012, "验证码不存在或已过期"),

    // ========== 用户模块 (2000-2999) ==========
    USER_NOT_FOUND(2000, "用户不存在"),
    USER_USERNAME_EXISTS(2001, "用户名已存在"),
    USER_PHONE_EXISTS(2002, "手机号已被其他用户使用"),
    USER_EMAIL_EXISTS(2003, "邮箱已被其他用户使用"),
    USER_USERNAME_FORMAT_INVALID(2004, "用户名格式不正确（4-32位，字母、数字、下划线）"),
    USER_DELETE_SUPER_ADMIN(2005, "禁止删除超级管理员"),
    USER_DISABLE_SELF(2006, "禁止禁用自己的账号"),

    // ========== 角色模块 (3000-3999) ==========
    ROLE_NOT_FOUND(3000, "角色不存在"),
    ROLE_CODE_EXISTS(3001, "角色编码已存在"),
    ROLE_CODE_FORMAT_INVALID(3002, "角色编码格式不正确（2-32位字母）"),
    ROLE_HAS_USERS(3003, "角色下存在关联用户，无法删除"),
    ROLE_DELETE_SUPER_ADMIN(3004, "禁止删除超级管理员角色"),

    // ========== 菜单模块 (4000-4999) ==========
    MENU_NOT_FOUND(4000, "菜单不存在"),
    MENU_HAS_CHILDREN(4001, "菜单下存在子菜单，无法删除"),
    MENU_HAS_BUTTONS(4002, "菜单下存在按钮权限，无法删除"),
    MENU_DELETE_ROOT(4003, "禁止删除根菜单"),

    // ========== 机构模块 (5000-5999) ==========
    ORG_NOT_FOUND(5000, "机构不存在"),
    ORG_CODE_EXISTS(5001, "机构编码已存在"),
    ORG_HAS_USERS(5002, "机构下存在用户，无法删除"),

    // ========== 字典模块 (6000-6999) ==========
    DICT_NOT_FOUND(6000, "字典不存在"),
    DICT_CODE_EXISTS(6001, "字典编码已存在"),
    DICT_DATA_KEY_EXISTS(6002, "字典数据键已存在"),

    // ========== 通用错误码 (9500-9999) ==========
    VALIDATION_ERROR(9500, "参数校验失败"),
    PARAM_MISSING(9501, "缺少必需参数"),
    PARAM_TYPE_ERROR(9502, "参数类型错误"),
    PERMISSION_DENIED(9503, "无权限访问"),
    SYSTEM_ERROR(9999, "系统内部错误");

    private final int code;
    private final String message;

    /** 转换为运行时异常抛出 */
    public RuntimeException toException() {
        return new BusinessException(this);
    }

    /** 业务异常内部类 */
    public static class BusinessException extends RuntimeException {
        private final ErrorCode errorCode;

        public BusinessException(ErrorCode errorCode) {
            super(errorCode.getMessage());
            this.errorCode = errorCode;
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }
    }
}
