package com.lest.common.core.web.domain;

import com.lest.common.core.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author yshan2028
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.OK)
    public R<Void> handleServiceException(ServiceException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return R.fail(message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return R.fail(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return R.fail(message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "缺少必需参数: " + e.getParameterName();
        log.warn(message);
        return R.fail(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数 '%s' 类型错误，期望类型: %s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn(message);
        return R.fail(message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = String.format("不支持的请求方法: %s", e.getMethod());
        log.warn(message);
        return R.fail(message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        String message = String.format("接口不存在: %s %s", e.getHttpMethod(), e.getRequestURL());
        log.warn(message);
        return R.fail(message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail("系统异常");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleThrowable(Throwable e) {
        log.error("系统错误", e);
        return R.fail("系统错误");
    }
}
