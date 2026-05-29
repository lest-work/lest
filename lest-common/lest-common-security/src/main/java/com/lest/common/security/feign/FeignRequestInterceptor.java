package com.lest.common.security.feign;

import com.lest.common.core.constant.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器，用于传递用户信息到下游服务
 *
 * @author yshan2028
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        String userId = httpServletRequest.getHeader(SecurityConstants.DETAILS_USER_ID);
        if (userId != null) {
            requestTemplate.header(SecurityConstants.DETAILS_USER_ID, userId);
        }

        String userKey = httpServletRequest.getHeader(SecurityConstants.USER_KEY);
        if (userKey != null) {
            requestTemplate.header(SecurityConstants.USER_KEY, userKey);
        }

        String userName = httpServletRequest.getHeader(SecurityConstants.DETAILS_USERNAME);
        if (userName != null) {
            requestTemplate.header(SecurityConstants.DETAILS_USERNAME, userName);
        }

        String authentication = httpServletRequest.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (authentication != null) {
            requestTemplate.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
        }
    }
}
