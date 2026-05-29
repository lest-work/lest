package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

    /** 令牌类型 */
    private String tokenType;

    /** 过期时间（秒） */
    private Long expiresIn;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;
}
