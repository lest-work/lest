package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单VO（字段名严格对齐数据库 sys_menu，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {

    /** 菜单ID */
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

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 子菜单列表（递归） */
    private List<MenuVO> children;

    /** 权限树回显选中状态（不持久化，仅 API 输出） */
    private Boolean checked;
}
