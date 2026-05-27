package com.lest.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跨模块通用错误码
 *
 * <p>错误码规则：
 * - 通用错误码: 9500-9999
 * </p>
 *
 * @author Lest
 * @since 2026-05-26
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode {

    /** 参数校验失败 */
    VALIDATION_ERROR(9500, "参数校验失败"),

    /** 缺少必需参数 */
    PARAM_MISSING(9501, "缺少必需参数"),

    /** 参数类型错误 */
    PARAM_TYPE_ERROR(9502, "参数类型错误"),

    /** 无权限访问 */
    PERMISSION_DENIED(9503, "无权限访问"),

    /** 系统内部错误 */
    SYSTEM_ERROR(9999, "系统内部错误");

    private final int code;
    private final String message;
}
