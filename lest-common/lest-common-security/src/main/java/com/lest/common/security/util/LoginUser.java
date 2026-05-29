package com.lest.common.security.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录用户信息
 *
 * @author yshan2028
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 用户类型 */
    private String userType;

    /** 用户来源 */
    private String fromSource;

    /** 角色标识集合 */
    private Set<String> roles;

    /** 权限标识集合 */
    private Set<String> permissions;

    /** 角色ID集合 */
    private Set<Long> roleIds;

    /** 登录时间 */
    private Long loginTime;

    /** 过期时间 */
    private Long expireTime;

    /** IP地址 */
    private String ipaddr;

    /** 登录令牌 */
    private String token;

    /** 账户是否过期 */
    private boolean accountNonExpired = true;

    /** 账户是否锁定 */
    private boolean accountNonLocked = true;

    /** 密码是否过期 */
    private boolean credentialsNonExpired = true;

    /** 账户是否可用 */
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions != null && !permissions.isEmpty()) {
            return permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(java.util.stream.Collectors.toSet());
        }
        return java.util.Collections.emptySet();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
