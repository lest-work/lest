package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单DTO（字段名严格对齐数据库 sys_menu，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class MenuDTO {

    /** 菜单ID（更新时必需） */
    private Long id;

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /** 菜单类型: 1=目录, 2=菜单, 3=按钮 */
    @NotNull(message = "菜单类型不能为空")
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

    /** 是否显示: 1=是, 0=否 */
    private Integer visible;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 是否缓存: 1=是, 0=否 */
    private Integer keepAlive;

    /** 是否总是显示: 1=是, 0=否 */
    private Integer alwaysShow;

    /** 重定向地址 */
    private String redirect;

    /** 路由元数据（JSON字符串） */
    private String meta;
}
