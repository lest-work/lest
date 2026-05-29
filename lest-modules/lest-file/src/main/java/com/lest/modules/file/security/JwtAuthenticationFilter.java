package com.lest.modules.file.security;

import com.lest.common.security.util.JwtUtils;
import com.lest.common.security.util.LoginUser;
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

/**
 * JWT 认证过滤器（文件微服务专用）
 * <p>
 * 轻量级实现：只解析 JWT 并从 Token Claims 中提取用户信息，
 * 不依赖数据库查询（roles 已在 lest-auth 签发 Token 时写入 claims）。
 * </p>
 *
 * @author yshan2028
 * @since 2026-05-27
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && JwtUtils.isValid(token)) {
                if (JwtUtils.isAccessToken(token)) {
                    Long userId = JwtUtils.getUserId(token);
                    String username = JwtUtils.getUsername(token);
                    String nickname = JwtUtils.getNickname(token);
                    List<String> roles = JwtUtils.getRoles(token);

                    LoginUser loginUser = new LoginUser(
                            userId, null, username, null, nickname, null, null, null, null,
                            new java.util.HashSet<>(roles), null, null,
                            null, null, null, null,
                            true, true, true, true);

                    List<SimpleGrantedAuthority> authorities = roles == null ? List.of() :
                            roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                    .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("文件微服务 JWT 认证成功: userId={}, username={}", userId, username);
                }
            }
        } catch (Exception e) {
            log.warn("文件微服务 JWT 认证失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/actuator/health") ||
               path.equals("/error");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
