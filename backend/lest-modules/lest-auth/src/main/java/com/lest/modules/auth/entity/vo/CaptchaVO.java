package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码VO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {

    /** 验证码UUID */
    private String uuid;

    /** Base64编码的图片 */
    private String image;

    /** 过期时间（秒） */
    private Long expireSeconds;
}
