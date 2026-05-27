package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class CaptchaDTO {

    /** 验证码UUID */
    @NotBlank(message = "验证码UUID不能为空")
    private String uuid;

    /** 验证码内容 */
    @NotBlank(message = "验证码内容不能为空")
    private String code;
}
