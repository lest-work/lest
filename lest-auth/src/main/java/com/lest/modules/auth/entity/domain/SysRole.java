package com.lest.modules.auth.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色实体，对应 sys_role 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 角色描述 */
    private String description;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 是否超管: 1=是, 0=否 */
    private Integer isSuper;

    /** 排序号 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记: 0=未删除, 1=已删除 */
    @TableLogic
    private Integer deleted;
}
