package com.lest.common.core.web.domain;

import com.lest.common.core.constant.HttpStatus;
import com.lest.common.core.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果
 *
 * @author yshan2028
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "code";
    public static final String MSG_TAG = "msg";
    public static final String DATA_TAG = "data";

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data)) {
            super.put(DATA_TAG, data);
        }
    }

    public static AjaxResult success() {
        return new AjaxResult(HttpStatus.SUCCESS, "操作成功");
    }

    public static AjaxResult success(String msg) {
        return new AjaxResult(HttpStatus.SUCCESS, msg);
    }

    public static AjaxResult success(Object data) {
        return new AjaxResult(HttpStatus.SUCCESS, "操作成功", data);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    public static AjaxResult warn() {
        return new AjaxResult(HttpStatus.WARN, "操作警告");
    }

    public static AjaxResult warn(String msg) {
        return new AjaxResult(HttpStatus.WARN, msg);
    }

    public static AjaxResult warn(String msg, Object data) {
        return new AjaxResult(HttpStatus.WARN, msg, data);
    }

    public static AjaxResult error() {
        return new AjaxResult(HttpStatus.ERROR, "操作失败");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(HttpStatus.ERROR, msg);
    }

    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }

    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    public static AjaxResult error(int code, String msg, Object data) {
        return new AjaxResult(code, msg, data);
    }

    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public AjaxResult code(int code) {
        this.put(CODE_TAG, code);
        return this;
    }

    public AjaxResult msg(String msg) {
        this.put(MSG_TAG, msg);
        return this;
    }

    public AjaxResult data(Object data) {
        this.put(DATA_TAG, data);
        return this;
    }

    public int getCode() {
        return (int) this.get(CODE_TAG);
    }

    public boolean isSuccess() {
        return HttpStatus.SUCCESS == getCode();
    }
}
