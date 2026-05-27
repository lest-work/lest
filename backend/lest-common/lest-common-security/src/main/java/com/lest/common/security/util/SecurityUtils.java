package com.lest.common.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 安全工具类
 *
 * @author Lest
 * @since 2026-05-26
 */
@Component
public class SecurityUtils {

    private static SecurityUtils instance;

    public SecurityUtils() {
        instance = this;
    }

    public static Long getUserId() {
        if (instance == null) {
            return null;
        }
        return instance.getCurrentUserId();
    }

    public static String getUsername() {
        if (instance == null) {
            return null;
        }
        return instance.getCurrentUsername();
    }

    public static LoginUser getLoginUser() {
        if (instance == null) {
            return null;
        }
        return instance.getCurrentUser();
    }

    public LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser user) {
            return user;
        }
        return null;
    }

    public Long getCurrentUserId() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public String getCurrentUsername() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public String getCurrentNickname() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getNickname() : null;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority) || a.getAuthority().equals("*:*"));
    }

    public List<GrantedAuthority> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream().collect(Collectors.toList());
        }
        return List.of();
    }
}
