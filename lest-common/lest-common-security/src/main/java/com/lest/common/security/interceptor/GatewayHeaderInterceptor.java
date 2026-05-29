package com.lest.common.security.interceptor;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.utils.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 网关 Header 拦截器，从网关注入的请求头中读取用户信息
 *
 * @author yshan2028
 */
public class GatewayHeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader(SecurityConstants.DETAILS_USER_ID);
        if (userId != null) {
            SecurityContextHolder.setUserId(Long.parseLong(userId));
        }

        String username = request.getHeader(SecurityConstants.DETAILS_USERNAME);
        if (username != null) {
            SecurityContextHolder.setUserName(username);
        }

        String userKey = request.getHeader(SecurityConstants.USER_KEY);
        if (userKey != null) {
            SecurityContextHolder.setUserKey(userKey);
        }

        return true;
    }
}
