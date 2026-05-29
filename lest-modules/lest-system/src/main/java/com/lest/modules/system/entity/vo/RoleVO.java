package com.lest.modules.system.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色VO（映射自 lest-auth RoleVO）
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {

    /** 角色ID */
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

    /** 权限标识列表 */
    private String[] permissions;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
