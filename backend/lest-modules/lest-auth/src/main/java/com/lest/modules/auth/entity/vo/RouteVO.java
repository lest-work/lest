package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由VO（用于前端路由生成）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteVO {

    /** 路由名称 */
    private String name;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 重定向地址 */
    private String redirect;

    /** 菜单图标 */
    private String icon;

    /** 隐藏菜单 */
    private Boolean hidden;

    /** 菜单元数据 */
    private MetaVO meta;

    /** 子路由列表 */
    private List<RouteVO> children;

    /**
     * 菜单元数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaVO {
        /** 菜单标题 */
        private String title;

        /** 菜单图标 */
        private String icon;

        /** 是否缓存 */
        private Boolean keepAlive;

        /** 是否总是显示 */
        private Boolean alwaysShow;
    }
}
