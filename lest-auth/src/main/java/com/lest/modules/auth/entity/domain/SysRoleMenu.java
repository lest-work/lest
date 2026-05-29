package com.lest.modules.auth.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色菜单关联实体
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    @TableId(type = IdType.INPUT)
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}
