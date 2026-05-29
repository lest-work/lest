package com.lest.modules.system.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户DTO（映射自 lest-auth UserDTO）
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Data
public class UserDTO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,32}$", message = "用户名格式不正确")
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

    /** 密码 */
    private String password;
}
