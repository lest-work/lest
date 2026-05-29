package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class LoginDTO {

    /** 验证码UUID（开发模式可为空） */
    private String uuid;

    /** 验证码内容（开发模式可为空） */
    private String captcha;

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
