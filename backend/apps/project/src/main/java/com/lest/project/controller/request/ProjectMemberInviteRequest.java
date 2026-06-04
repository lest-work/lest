package com.lest.project.controller.request;

import jakarta.validation.constraints.NotNull;

/**
 * 邀请项目成员请求体（按用户ID添加现有成员）。
 */
public class ProjectMemberInviteRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "项目角色不能为空")
    private String role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
