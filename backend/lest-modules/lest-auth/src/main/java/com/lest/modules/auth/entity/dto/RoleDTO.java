package com.lest.modules.auth.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 角色DTO，用于接收前端提交的创建/更新请求
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class RoleDTO {

    /** 角色ID（更新时必需） */
    @JsonProperty("roleId")
    private Long id;

    /** 角色编码（创建时必需） */
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[a-zA-Z]{2,32}$", message = "角色编码格式不正确（2-32位字母）")
    private String roleCode;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 角色描述 */
    private String description;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 排序号 */
    private Integer sort;

    /** 菜单ID列表 */
    private Long[] menuIds;

    // ===== 以下为前端期望的字段名_alias =====

    /** 前端字段 alias: 备注/描述 */
    @JsonProperty("comments")
    private String comments;

    public void setComments(String comments) {
        if (this.description == null && comments != null) {
            this.description = comments;
        }
        this.comments = comments;
    }
}
