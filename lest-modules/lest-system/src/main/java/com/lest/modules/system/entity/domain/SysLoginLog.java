package com.lest.modules.system.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 登录日志实体，对应 sys_login_log 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("sys_login_log")
public class SysLoginLog {

    /** 日志ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 登录类型：0-登录成功，1-登录失败，2-退出登录，3-刷新Token */
    private Integer loginType;

    /** IP地址 */
    private String ipAddress;

    /** User-Agent信息 */
    private String userAgent;

    /** 操作系统 */
    private String os;

    /** 浏览器 */
    private String browser;

    /** 设备名称 */
    private String device;

    /** 状态：0-失败，1-成功 */
    private Integer status;

    /** 消息/备注 */
    private String msg;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
