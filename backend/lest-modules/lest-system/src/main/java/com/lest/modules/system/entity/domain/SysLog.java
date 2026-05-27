package com.lest.modules.system.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 操作日志实体，对应 sys_log 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("sys_log")
public class SysLog {

    /** 日志ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 操作模块（如：用户管理、角色管理） */
    private String module;

    /** 操作功能描述 */
    private String description;

    /** 操作类型（如：CREATE、UPDATE、DELETE） */
    private String operation;

    /** HTTP请求方法（GET/POST/PUT/DELETE） */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 请求参数 */
    private String requestParams;

    /** 请求体 */
    private String requestBody;

    /** 响应状态码 */
    private Integer responseStatus;

    /** 响应内容 */
    private String responseBody;

    /** IP地址 */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 操作系统 */
    private String os;

    /** 浏览器 */
    private String browser;

    /** 设备 */
    private String device;

    /** 执行时间（毫秒） */
    private Integer executionTime;

    /** 错误信息 */
    private String errorMessage;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
