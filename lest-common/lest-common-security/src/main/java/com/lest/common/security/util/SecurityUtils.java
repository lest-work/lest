package com.lest.common.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.utils.SecurityContextHolder;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;

/**
 * 权限获取工具类
 *
 * @author yshan2028
 */
public class SecurityUtils {

    private static final String TOKEN_PREFIX = "Bearer ";

    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }

    public static String getUserKey() {
        return SecurityContextHolder.getUserKey();
    }

    public static LoginUser getLoginUser() {
        return (LoginUser) SecurityContextHolder.get(SecurityConstants.LOGIN_USER);
    }

    public static String getToken() {
        return getToken(ServletUtils.getHttpServletRequest());
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replaceFirst(TOKEN_PREFIX, "");
        }
        return token;
    }

    public static boolean isAdmin() {
        return isAdmin(getUserId());
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && userId == 1L;
    }

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
