package com.lest.project.controller.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新项目成员角色请求体。
 */
public class ProjectMemberRoleUpdateRequest {

    @NotBlank(message = "项目角色不能为空")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
