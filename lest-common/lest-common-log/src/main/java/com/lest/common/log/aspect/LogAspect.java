package com.lest.common.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.domain.SysOperLog;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.log.filter.PropertyPreExcludeFilter;
import com.lest.common.log.service.AsyncLogService;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志记录切面
 * <p>
 * </p>
 *
 * @author yshan2028
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    private final ObjectMapper objectMapper;

    private final AsyncLogService asyncLogService;

    public LogAspect(@Autowired(required = false) AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
        this.objectMapper = new ObjectMapper();
        // 配置 ObjectMapper 过滤敏感字段
        SimpleBeanPropertyFilter filter = new PropertyPreExcludeFilter();
        this.objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("propertyFilter", filter));
    }

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.lest.common.log.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 前置通知 - 记录开始时间
     */
    @Before("logPointcut()")
    public void before(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());
    }

    /**
     * 后置通知 - 记录成功日志
     */
    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        try {
            handleLog(joinPoint, null, result);
        } finally {
            START_TIME.remove();
        }
    }

    /**
     * 异常通知 - 记录异常日志
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        try {
            handleLog(joinPoint, e, null);
        } finally {
            START_TIME.remove();
        }
    }

    /**
     * 处理日志
     */
    private void handleLog(JoinPoint joinPoint, Exception e, Object result) {
        Log controllerLog = getAnnotation(joinPoint);
        if (controllerLog == null) {
            return;
        }

        SysOperLog operLog = new SysOperLog();

        // 获取开始时间
        Long startTime = START_TIME.get();
        long costTime = 0L;
        if (startTime != null) {
            costTime = System.currentTimeMillis() - startTime;
        }

        // 设置操作时间
        operLog.setOperateTime(new Date());

        // 设置消耗时间
        operLog.setCostTime(costTime);

        // 设置业务操作类型
        operLog.setBusinessType(controllerLog.businessType().ordinal());

        // 设置操作人员类型
        operLog.setOperatorType(controllerLog.operatorType().ordinal());

        // 设置操作标题
        operLog.setTitle(controllerLog.title());

        // 设置请求方法
        operLog.setRequestMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        // 设置请求IP
        operLog.setRequestIp(getIpAddress());

        // 设置请求URL
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            operLog.setRequestUrl(request.getRequestURI());
        }

        // 设置请求参数
        if (controllerLog.isSaveRequestData()) {
            operLog.setRequestParams(getRequestParams(joinPoint));
        }

        // 设置返回参数
        if (controllerLog.isSaveResponseData() && result != null) {
            operLog.setJsonResult(getResponseResult(result));
        }

        // 设置错误信息
        if (e != null) {
            operLog.setStatus(1);
            operLog.setErrorMsg(getErrorMsg(e));
        } else {
            operLog.setStatus(0);
        }

        // 异步保存日志
        if (asyncLogService != null) {
            asyncLogService.saveLog(operLog);
        }

        // 输出日志
        if (e != null) {
            log.error("操作日志记录失败: {}", e.getMessage());
        }
    }

    /**
     * 获取注解
     */
    private Log getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(signature.getName())) {
                Log annotation = method.getAnnotation(Log.class);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return null;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint) {
        String params = "";
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg == null) {
                    continue;
                }
                // 跳过 HttpServletRequest 和 MultipartFile
                if (arg instanceof HttpServletRequest || arg instanceof MultipartFile) {
                    continue;
                }
                try {
                    // 过滤敏感字段
                    String json = objectMapper.writeValueAsString(arg);
                    params = json;
                    break;
                } catch (Exception e) {
                    log.warn("序列化请求参数失败: {}", e.getMessage());
                }
            }
        }
        return params;
    }

    /**
     * 获取响应结果
     */
    private String getResponseResult(Object result) {
        try {
            // 过滤敏感字段
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.warn("序列化响应结果失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取错误信息
     */
    private String getErrorMsg(Exception e) {
        String errorMsg = e.getClass().getName();
        String message = e.getMessage();
        if (StringUtils.isNotEmpty(message)) {
            errorMsg += ": " + message;
        }
        // 截断过长错误信息
        if (errorMsg.length() > 2000) {
            errorMsg = errorMsg.substring(0, 2000);
        }
        return errorMsg;
    }

    /**
     * 获取客户端IP
     */
    private String getIpAddress() {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "localhost";
            }
        }
        // 多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
