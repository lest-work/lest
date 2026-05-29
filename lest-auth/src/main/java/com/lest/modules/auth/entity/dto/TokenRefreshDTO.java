package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token刷新DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class TokenRefreshDTO {

    /** 刷新令牌 */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
