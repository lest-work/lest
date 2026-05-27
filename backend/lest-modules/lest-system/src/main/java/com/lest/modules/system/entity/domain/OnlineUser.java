package com.lest.modules.system.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 在线用户会话信息，存储在Redis中
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUser {

    /** 会话ID，对应Redis Key的后缀 */
    private String sessionId;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 登录IP */
    private String ipAddress;

    /** 操作系统 */
    private String os;

    /** 浏览器 */
    private String browser;

    /** 设备名称 */
    private String device;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 最后访问时间（用于判断活跃度） */
    private LocalDateTime lastAccessTime;
}
