package com.lest.common.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 统一 JWT 工具类
 * <p>
 * 提供 JWT 令牌的生成、解析、验证等操作，供所有微服务模块使用。
 * 所有业务模块应通过此类进行 JWT 操作，禁止在各模块中重复实现。
 * </p>
 * <p>
 * 设计说明：
 * <ul>
 *   <li>Token 格式采用 HS256 对称加密，密钥统一在配置中管理</li>
 *   <li>支持 access token 和 refresh token 两种类型</li>
 *   <li>每个 Token 携带唯一标识 JTI，用于 token 黑名单管理</li>
 *   <li>Claims 包含: userId, username, nickname, roles, type, jti</li>
 * </ul>
 * </p>
 */
public class JwtUtils {

    private static final String SECRET = "lest-platform-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm";
    private static final long ACCESS_TOKEN_EXPIRATION = 900000L;   // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000L; // 7 days
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_JTI = "jti";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_NICKNAME = "nickname";
    private static final String CLAIM_ROLES = "roles";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtUtils() {}

    // ============================================================
    // Token 生成
    // ============================================================

    /**
     * 生成访问令牌 (Access Token)
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param nickname 用户昵称
     * @param roles    角色列表
     * @return JWT 访问令牌
     */
    public static String createAccessToken(Long userId, String username, String nickname, List<String> roles) {
        return createToken(userId, username, nickname, roles, TOKEN_TYPE_ACCESS, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成访问令牌 (不含昵称和角色)
     */
    public static String createAccessToken(Long userId, String username) {
        return createToken(userId, username, null, null, TOKEN_TYPE_ACCESS, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成刷新令牌 (Refresh Token)
     *
     * @param userId 用户ID
     * @return JWT 刷新令牌
     */
    public static String createRefreshToken(Long userId) {
        return createToken(userId, null, null, null, TOKEN_TYPE_REFRESH, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * 通用 Token 生成
     */
    private static String createToken(Long userId, String username, String nickname,
                                     List<String> roles, String type, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TYPE, type);
        claims.put(CLAIM_JTI, UUID.randomUUID().toString());
        claims.put("userId", userId);

        if (username != null) {
            claims.put(CLAIM_USERNAME, username);
        }
        if (nickname != null) {
            claims.put(CLAIM_NICKNAME, nickname);
        }
        if (roles != null) {
            claims.put(CLAIM_ROLES, roles);
        }

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .issuer("lest-platform")
                .signWith(KEY, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 兼容旧方法：使用默认 Claims 创建 Token
     */
    public static String createToken(Long userId, String username) {
        return createAccessToken(userId, username);
    }

    // ============================================================
    // Token 解析
    // ============================================================

    /**
     * 解析 Token，获取 Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证 Token 签名是否有效
     */
    public static boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // Claims 提取
    // ============================================================

    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            Object userId = claims.get("userId");
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户名
     */
    public static String getUsername(String token) {
        try {
            Claims claims = parseToken(token);
            String username = claims.get(CLAIM_USERNAME, String.class);
            return username != null ? username : claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户昵称
     */
    public static String getNickname(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_NICKNAME, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取角色列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_ROLES, List.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 Token 类型 (access / refresh)
     */
    public static String getTokenType(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_TYPE, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 JTI (Token 唯一标识)
     */
    public static String getJti(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_JTI, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否为访问令牌
     */
    public static boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    /**
     * 判断是否为刷新令牌
     */
    public static boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(getTokenType(token));
    }

    /**
     * 将角色列表转换为 Spring Security 权限格式 (自动加 ROLE_ 前缀)
     */
    public static List<String> getAuthorities(String token) {
        List<String> roles = getRoles(token);
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toList();
    }

    // ============================================================
    // Token 有效期
    // ============================================================

    public static long getAccessTokenExpirationSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }

    public static long getRefreshTokenExpirationSeconds() {
        return REFRESH_TOKEN_EXPIRATION / 1000;
    }
}
