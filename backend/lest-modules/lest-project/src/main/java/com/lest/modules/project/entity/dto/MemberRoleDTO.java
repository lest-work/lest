package com.lest.modules.project.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 角色更新DTO
 */
@Data
public class MemberRoleDTO {
    /**
     * 角色：admin / developer / observer
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(admin|developer|observer)$", message = "角色只能是 admin、developer 或 observer")
    private String role;
}
