package com.lest.common.core;

/**
 * 错误码枚举基类
 *
 * @author yshan2028
 */
public enum CommonErrorCode implements ErrorCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    ERR(500, "操作异常"),
    PARAM_NOT_NULL(400, "参数不能为空"),
    PARAM_TYPE_ERROR(400, "参数类型错误"),
    PARAM_FORMAT_ERROR(400, "参数格式错误"),
    UNAUTHORIZED(401, "未授权"),
    TOKEN_EXPIRE(401, "token已过期"),
    TOKEN_INVALID(401, "token无效"),
    AUTH_ERROR(401, "认证失败"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    private final int code;
    private final String message;

    CommonErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
