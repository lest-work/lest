package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户VO（字段名严格对齐数据库 sys_user，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 性别: 0=未知, 1=男, 2=女 */
    private Integer sex;

    /** 状态: 1=正常, 0=禁用 */
    private Integer status;

    /** 机构ID */
    private Long orgId;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 机构名称（关联 sys_organization.org_name） */
    private String orgName;

    /** 角色编码列表（关联 sys_role.role_code） */
    private String[] roles;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
