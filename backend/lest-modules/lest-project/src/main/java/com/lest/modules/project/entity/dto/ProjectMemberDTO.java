package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 项目成员DTO
 */
@Data
public class ProjectMemberDTO {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色：admin / developer / observer
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(admin|developer|observer)$", message = "角色只能是 admin、developer 或 observer")
    private String role;
}
