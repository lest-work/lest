package com.lest.modules.auth.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码实体
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
@TableName("auth_captcha")
public class AuthCaptcha implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** UUID作为主键 */
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /** 验证码内容 */
    private String code;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
