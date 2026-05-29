package com.lest.modules.system.entity.dto;

/**
 * 登录日志查询DTO，支持多条件分页查询
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public record LoginLogQueryDTO(
    /** 用户名（模糊匹配） */
    String username,
    /** 用户昵称（模糊匹配） */
    String nickname,
    /** 登录类型：0-登录成功，1-登录失败，2-退出登录，3-刷新Token */
    Integer loginType,
    /** 开始时间（格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss） */
    String startTime,
    /** 结束时间（格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss） */
    String endTime,
    /** 页码，默认1 */
    Integer page,
    /** 每页大小，默认20 */
    Integer size
) {}
