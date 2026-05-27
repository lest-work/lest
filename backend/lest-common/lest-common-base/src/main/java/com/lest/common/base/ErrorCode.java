package com.lest.common.base;

/**
 * 错误码接口，各模块 ErrorCode 枚举需实现此接口
 *
 * @author Lest
 * @since 2026-05-26
 */
public interface ErrorCode {
    int getCode();
    String getMessage();
}
