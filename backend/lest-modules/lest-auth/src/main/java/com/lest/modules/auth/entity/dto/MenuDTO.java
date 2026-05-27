package com.lest.modules.auth.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单DTO，用于接收前端提交的创建/更新请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class MenuDTO {

    /** 菜单ID（更新时必需） */
    @JsonProperty("menuId")
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

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 菜单名称 */
    @JsonProperty("title")
    private String title;

    /** 前端字段 alias: 权限标识 */
    @JsonProperty("authority")
    private String authority;

    /** 前端字段 alias: 菜单ID */
    @JsonProperty("parentId")
    private Long _parentId;

    // 同步 alias 值到实际字段
    public void setTitle(String title) {
        if (this.menuName == null && title != null) {
            this.menuName = title;
        }
        this.title = title;
    }

    public void setAuthority(String authority) {
        if (this.permission == null && authority != null) {
            this.permission = authority;
        }
        this.authority = authority;
    }

    public void set_parentId(Long _parentId) {
        if (this.parentId == null && _parentId != null) {
            this.parentId = _parentId;
        }
        this._parentId = _parentId;
    }
}
