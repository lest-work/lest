package com.lest.common.core;

/**
 * 统一响应结果（保留旧名，向后兼容）
 *
 * @author yshan2028
 * @deprecated use {@link com.lest.common.core.web.domain.R}
 */
@Deprecated
public class Result<T> extends com.lest.common.core.web.domain.R<T> {

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(String msg, T data) {
        Result<T> result = ok();
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return ok();
    }

    public static <T> Result<T> success(T data) {
        return ok(data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return ok(msg, data);
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg("操作失败");
        return result;
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public boolean isSuccess() {
        return this.getCode() == 200;
    }

    public Result<T> traceId(String traceId) {
        return this;
    }
}
