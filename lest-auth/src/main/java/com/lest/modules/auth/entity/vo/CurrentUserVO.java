package com.lest.modules.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前用户VO（字段名严格对齐数据库，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserVO {

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

    /** 机构ID */
    private Long orgId;

    /** 机构名称 */
    private String orgName;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限标识列表 */
    private List<String> permissions;

    /** 路由列表 */
    private List<RouteVO> routes;
}
