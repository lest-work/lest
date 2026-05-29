package com.lest.modules.auth.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统菜单实体，对应 sys_menu 表
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型: 1=目录, 2=菜单, 3=按钮 */
    private Integer menuType;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识 */
    private String permission;

    /** 菜单图标 */
    private String icon;

    /** 排序号 */
    private Integer sort;

    /** 是否可见: 1=可见, 0=隐藏 */
    private Integer visible;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 是否缓存: 1=缓存, 0=不缓存 */
    private Integer keepAlive;

    /** 是否总是显示: 1=是, 0=否 */
    private Integer alwaysShow;

    /** 重定向地址 */
    private String redirect;

    /** 路由元数据（JSON字符串） */
    private String meta;

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
