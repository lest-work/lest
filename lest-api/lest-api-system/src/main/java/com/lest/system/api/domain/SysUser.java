package com.lest.system.api.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统用户实体（无 MyBatis 注解，供跨服务共享）
 *
 * @author yshan2028
 */
@Data
public class SysUser implements Serializable {

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
     * 性别: 0=未知, 1=男, 2=女
     */
    private Integer sex;

    /**
     * 状态: 1=正常, 0=禁用
     */
    private Integer status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private String loginDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 机构ID
     */
    private Long deptId;
}
