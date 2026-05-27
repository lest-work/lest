package com.lest.modules.auth.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户DTO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class UserDTO {

    /** 用户ID（更新时必需） */
    private Long id;

    /** 用户名（创建时必需） */
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,32}$", message = "用户名格式不正确（4-32位，字母、数字、下划线）")
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 性别: 0=未知, 1=男, 2=女 */
    private Integer sex;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 机构ID */
    private Long orgId;

    /** 角色ID列表 */
    private Long[] roleIds;
}
