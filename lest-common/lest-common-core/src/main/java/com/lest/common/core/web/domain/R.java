package com.lest.common.core.web.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lest.common.core.constant.HttpStatus;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @author yshan2028
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public R() {
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<>(HttpStatus.SUCCESS, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return new R<>(HttpStatus.SUCCESS, "操作成功", data);
    }

    public static <T> R<T> ok(String msg) {
        return new R<>(HttpStatus.SUCCESS, msg);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(HttpStatus.SUCCESS, msg, data);
    }

    public static <T> R<T> fail() {
        return new R<>(HttpStatus.ERROR, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(HttpStatus.ERROR, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> fail(T data) {
        return new R<>(HttpStatus.ERROR, "操作失败", data);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(HttpStatus.ERROR, msg, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return HttpStatus.SUCCESS == code;
    }
}
