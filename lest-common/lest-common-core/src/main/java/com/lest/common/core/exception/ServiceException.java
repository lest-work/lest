package com.lest.common.core.exception;

import com.lest.common.core.constant.HttpStatus;

/**
 * 业务异常
 *
 * @author yshan2028
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private Object detailMessage;

    public ServiceException() {
        this.code = HttpStatus.ERROR;
        this.message = "业务异常";
    }

    public ServiceException(String message) {
        this.message = message;
        this.code = HttpStatus.ERROR;
    }

    public ServiceException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(int code, String message, Object detailMessage) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public ServiceException(String message, Object detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
        this.code = HttpStatus.ERROR;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getDetailMessage() {
        return detailMessage;
    }
}
