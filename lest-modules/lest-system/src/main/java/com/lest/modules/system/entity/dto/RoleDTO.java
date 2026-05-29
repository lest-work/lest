package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 角色DTO（映射自 lest-auth RoleDTO）
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Data
public class RoleDTO {

    /** 角色ID */
    private Long id;

    /** 角色编码 */
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[a-zA-Z_]{2,32}$", message = "角色编码格式不正确")
    private String roleCode;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 角色描述 */
    private String description;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 是否超管: 1=是, 0=否 */
    private Integer isSuper;

    /** 排序号 */
    private Integer sort;

    /** 菜单ID列表 */
    private Long[] menuIds;
}
