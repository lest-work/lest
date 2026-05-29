package com.lest.common.core.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author yshan2028
 */
public class JwtUtils {

    private static final String SECRET = "LestPlatformSecretKeyForJwtTokenGenerationAndValidation2024MustBeLongEnough";
    private static final long EXPIRATION = 720 * 60 * 1000L; // 720 分钟

    private JwtUtils() {
    }

    public static String createToken(String username) {
        return createToken(username, null);
    }

    public static String createToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    public static String parseToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean isValid(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static String getUsername(String token) {
        return parseToken(token);
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = getClaims(token);
            Object userId = claims.get("userId");
            if (userId == null) {
                return null;
            }
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
            return Long.parseLong(userId.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(SECRET.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String refreshToken(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        return createToken(username, claims);
    }
}
