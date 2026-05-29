package com.lest.system.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息（精简版，供 Feign 传输）
 *
 * @author yshan2028
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 状态: 1=正常, 0=禁用
     */
    private Integer status;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 权限列表
     */
    private String[] permissions;
}
