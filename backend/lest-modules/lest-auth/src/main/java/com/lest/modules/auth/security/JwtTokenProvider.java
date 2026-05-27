package com.lest.modules.auth.security;

import com.lest.common.security.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token 提供器（认证服务专用）
 * <p>
 * 提供认证服务专属的 JWT 操作，包括完整的 token 生成、解析、验证能力，
 * 以及 refresh token 黑名单管理。所有 JWT 核心逻辑委托给 {@link JwtUtils}。
 * </p>
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String REFRESH_BLACKLIST_PREFIX = "jwt:refresh:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public JwtTokenProvider(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username, String nickname, String[] roles) {
        return JwtUtils.createAccessToken(userId, username, nickname,
                roles != null ? List.of(roles) : null);
    }

    /**
     * 生成刷新令牌（同时存入 Redis 黑名单追踪）
     */
    public String generateRefreshToken(Long userId) {
        String token = JwtUtils.createRefreshToken(userId);
        String jti = JwtUtils.getJti(token);
        String blacklistKey = REFRESH_BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(blacklistKey, String.valueOf(userId),
                JwtUtils.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
        log.info("生成刷新令牌: userId={}, jti={}", userId, jti);
        return token;
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        return JwtUtils.isValid(token);
    }

    /**
     * 解析 Token
     */
    public io.jsonwebtoken.Claims parseToken(String token) {
        return JwtUtils.parseToken(token);
    }

    /**
     * 获取用户ID
     */
    public Long getUserId(String token) {
        return JwtUtils.getUserId(token);
    }

    /**
     * 获取用户名
     */
    public String getUsername(String token) {
        return JwtUtils.getUsername(token);
    }

    /**
     * 获取昵称
     */
    public String getNickname(String token) {
        return JwtUtils.getNickname(token);
    }

    /**
     * 获取 Token 类型
     */
    public String getTokenType(String token) {
        return JwtUtils.getTokenType(token);
    }

    /**
     * 获取 JTI
     */
    public String getJti(String token) {
        return JwtUtils.getJti(token);
    }

    /**
     * 判断是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        return JwtUtils.isAccessToken(token);
    }

    /**
     * 判断是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        return JwtUtils.isRefreshToken(token);
    }

    /**
     * 刷新令牌加入黑名单（退出登录时调用）
     */
    public void blacklistRefreshToken(String token) {
        String jti = JwtUtils.getJti(token);
        if (jti == null) return;
        String blacklistKey = REFRESH_BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(blacklistKey, "1",
                JwtUtils.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
        log.info("刷新令牌已加入黑名单: jti={}", jti);
    }

    /**
     * 检查刷新令牌是否在黑名单中
     */
    public boolean isRefreshTokenBlacklisted(String token) {
        String jti = JwtUtils.getJti(token);
        if (jti == null) return true;
        return Boolean.TRUE.equals(redisTemplate.hasKey(REFRESH_BLACKLIST_PREFIX + jti));
    }

    public long getAccessTokenExpirationSeconds() {
        return JwtUtils.getAccessTokenExpirationSeconds();
    }

    public long getRefreshTokenExpirationSeconds() {
        return JwtUtils.getRefreshTokenExpirationSeconds();
    }
}
