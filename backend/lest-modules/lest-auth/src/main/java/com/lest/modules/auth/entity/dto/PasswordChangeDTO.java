package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码修改DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class PasswordChangeDTO {

    /** 当前密码 */
    @NotBlank(message = "当前密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
