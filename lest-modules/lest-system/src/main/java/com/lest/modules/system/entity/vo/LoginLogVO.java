package com.lest.modules.system.entity.vo;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象（字段名严格对齐数据库 sys_login_log，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LoginLogVO(
    Long id,
    Long userId,
    String username,
    String nickname,
    Integer loginType,
    String ipAddress,
    String userAgent,
    String os,
    String browser,
    String device,
    Integer status,
    String msg,
    LocalDateTime createdAt
) {}
