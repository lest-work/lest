package com.lest.common.base;

import lombok.Data;

/**
 * 统一响应结果
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private String traceId;
    private long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(String message, T data) {
        Result<T> result = ok();
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return ok();
    }

    public static <T> Result<T> success(T data) {
        return ok(data);
    }

    public static <T> Result<T> success(String message, T data) {
        return ok(message, data);
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(CommonErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> Result<T> error(CommonErrorCode errorCode, String message) {
        return error(errorCode.getCode(), message);
    }

    public Result<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }
}
