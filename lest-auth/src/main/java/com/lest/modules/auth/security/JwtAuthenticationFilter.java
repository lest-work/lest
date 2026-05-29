package com.lest.modules.auth.security;

import com.lest.common.security.util.LoginUser;
import com.lest.modules.auth.mapper.SysRoleMapper;
import com.lest.modules.auth.mapper.SysUserRoleMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * <p>
 * 拦截所有请求，解析JWT Token并设置认证信息到SecurityContext
 * </p>
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                  SysUserRoleMapper userRoleMapper,
                                  SysRoleMapper roleMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 检查是否为访问令牌
                if (jwtTokenProvider.isAccessToken(token)) {
                    Long userId = jwtTokenProvider.getUserId(token);
                    String username = jwtTokenProvider.getUsername(token);
                    String nickname = jwtTokenProvider.getNickname(token);

                    // 构建认证信息
                    List<String> roleList = getRolesList(token);
                    boolean isSuper = checkIsSuper(userId);
                    LoginUser loginUser = new LoginUser();
                    loginUser.setUserId(userId);
                    loginUser.setUsername(username);
                    loginUser.setNickname(nickname);
                    loginUser.setRoles(Set.copyOf(roleList));
                    loginUser.setRoleIds(Set.of());
                    loginUser.setDeptId(null);
                    loginUser.setEnabled(true);
                    loginUser.setAccountNonExpired(true);
                    loginUser.setAccountNonLocked(true);
                    loginUser.setCredentialsNonExpired(true);
                    List<SimpleGrantedAuthority> authorities = getAuthorities(token);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, authorities);

                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("用户认证成功: userId={}, username={}", userId, username);
                }
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取Token
     *
     * @param request HTTP请求
     * @return Token字符串
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 从Token中获取权限列表
     *
     * @param token JWT令牌
     * @return 权限列表
     */
    private List<String> getRolesList(String token) {
        try {
            var claims = jwtTokenProvider.parseToken(token);
            var roles = (java.util.List<?>) claims.get("roles", java.util.List.class);
            if (roles != null) {
                return roles.stream()
                        .map(role -> (String) role)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取角色列表失败: {}", e.getMessage());
        }
        return java.util.Collections.emptyList();
    }

    private List<SimpleGrantedAuthority> getAuthorities(String token) {
        try {
            var claims = jwtTokenProvider.parseToken(token);
            var roles = (java.util.List<?>) claims.get("roles", java.util.List.class);
            if (roles != null) {
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + (String) role))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("获取权限列表失败: {}", e.getMessage());
        }
        return List.of();
    }

    private boolean checkIsSuper(Long userId) {
        var userRoles = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lest.modules.auth.entity.domain.SysUserRole>()
                        .eq(com.lest.modules.auth.entity.domain.SysUserRole::getUserId, userId));
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        List<Long> roleIds = userRoles.stream()
                .map(com.lest.modules.auth.entity.domain.SysUserRole::getRoleId)
                .collect(Collectors.toList());
        var roles = roleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.lest.modules.auth.entity.domain.SysRole>()
                        .in(com.lest.modules.auth.entity.domain.SysRole::getId, roleIds));
        return roles.stream().anyMatch(r -> r != null && Integer.valueOf(1).equals(r.getIsSuper()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 忽略不需要认证的路径
        return path.startsWith("/auth/captcha") ||
               path.startsWith("/auth/login") ||
               path.startsWith("/auth/refresh") ||
               path.equals("/error");
    }
}
