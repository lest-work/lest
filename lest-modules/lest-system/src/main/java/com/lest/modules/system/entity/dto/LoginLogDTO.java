package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 登录日志保存DTO，用于接收外部写入的日志记录
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LoginLogDTO(
    /** 用户ID */
    Long userId,
    /** 用户名 */
    String username,
    /** 用户昵称 */
    String nickname,
    /** 登录类型：0-登录成功，1-登录失败，2-退出登录，3-刷新Token */
    @NotNull(message = "登录类型不能为空")
    Integer loginType,
    /** IP地址 */
    String ipAddress,
    /** User-Agent信息 */
    String userAgent,
    /** 操作系统 */
    String os,
    /** 浏览器 */
    String browser,
    /** 设备名称 */
    String device,
    /** 状态：0-失败，1-成功 */
    @NotNull(message = "状态不能为空")
    Integer status,
    /** 消息/备注 */
    String msg
) {}
