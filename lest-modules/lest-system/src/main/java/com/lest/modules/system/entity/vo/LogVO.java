package com.lest.modules.system.entity.vo;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象（字段名严格对齐数据库 sys_log，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LogVO(
    Long id,
    Long userId,
    String username,
    String nickname,
    String module,
    String description,
    String operation,
    String requestMethod,
    String requestUrl,
    String requestParams,
    String requestBody,
    Integer responseStatus,
    String responseBody,
    String ipAddress,
    String userAgent,
    String os,
    String browser,
    String device,
    Integer executionTime,
    String errorMessage,
    LocalDateTime createdAt
) {}
