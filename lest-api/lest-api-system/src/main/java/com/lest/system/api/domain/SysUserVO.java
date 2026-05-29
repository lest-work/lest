package com.lest.system.api.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户VO（供 Feign 传输，跨服务共享）
 *
 * @author yshan2028
 */
@Data
public class SysUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别: 0=未知, 1=男, 2=女
     */
    private Integer sex;

    /**
     * 状态: 1=正常, 0=禁用
     */
    private Integer status;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 角色编码列表
     */
    private String[] roles;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
