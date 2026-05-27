package com.lest.modules.auth.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单VO，用于API返回
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {

    private Long id;
    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String permission;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private Integer keepAlive;
    private Integer alwaysShow;
    private String redirect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MenuVO> children;

    /** 权限树回显选中状态（Lombok @Data 会自动生成 getter/setter） */
    private Boolean checked;

    // ===== 以下为前端期望的字段名_alias =====

    @JsonProperty("menuId")
    public Long getMenuId() { return this.id; }

    @JsonProperty("title")
    public String getTitle() { return this.menuName; }

    @JsonProperty("authority")
    public String getAuthority() { return this.permission; }

    @JsonProperty("sortNumber")
    public Integer getSortNumber() { return this.sort; }

    /** 前端 alias: 是否隐藏（0-否, 1-是） */
    @JsonProperty("hide")
    public Integer getHide() {
        return (this.visible != null && this.visible == 0) ? 1 : 0;
    }

    /** 前端 alias: 菜单类型（0=菜单, 1=按钮） */
    @JsonProperty("menuType")
    public Integer getMenuType() {
        if (this.menuType == null) return null;
        return switch (this.menuType) {
            case 1 -> 0;  // 后端目录 -> 前端菜单
            case 2 -> 1;  // 后端菜单 -> 前端按钮
            case 3 -> 2;  // 后端按钮 -> 前端外链
            default -> this.menuType;
        };
    }

    @JsonProperty("createTime")
    public LocalDateTime getCreateTime() { return this.createdAt; }
}
