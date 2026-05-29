package com.lest.modules.system.entity.vo;

import java.time.LocalDateTime;

/**
 * 在线用户视图对象，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record OnlineUserVO(
    /** 会话ID */
    String sessionId,
    /** 用户ID */
    Long userId,
    /** 用户名 */
    String username,
    /** 用户昵称 */
    String nickname,
    /** 登录IP */
    String ipAddress,
    /** 操作系统 */
    String os,
    /** 浏览器 */
    String browser,
    /** 设备名称 */
    String device,
    /** 登录时间 */
    LocalDateTime loginTime,
    /** 最后访问时间 */
    LocalDateTime lastAccessTime
) {}
