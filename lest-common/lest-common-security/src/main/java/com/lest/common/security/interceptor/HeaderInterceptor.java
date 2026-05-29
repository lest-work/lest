package com.lest.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.utils.SecurityContextHolder;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;
import com.lest.common.security.auth.AuthUtil;
import com.lest.common.security.util.SecurityUtils;
import com.lest.common.security.util.LoginUser;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 *
 * @author yshan2028
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String userIdStr = ServletUtils.getHeader(SecurityConstants.DETAILS_USER_ID);
        if (StringUtils.isNotEmpty(userIdStr)) {
            SecurityContextHolder.setUserId(Long.parseLong(userIdStr));
        }
        SecurityContextHolder.setUserName(ServletUtils.getHeader(SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(SecurityConstants.USER_KEY));

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
