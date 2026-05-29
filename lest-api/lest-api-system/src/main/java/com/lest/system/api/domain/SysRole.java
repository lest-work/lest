package com.lest.system.api.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统角色实体（无 MyBatis 注解，供跨服务共享）
 *
 * @author yshan2028
 */
@Data
public class SysRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态: 1=正常, 0=禁用
     */
    private Integer status;

    /**
     * 是否超管: 1=是, 0=否
     */
    private Integer isSuper;

    /**
     * 排序号
     */
    private Integer sort;
}
