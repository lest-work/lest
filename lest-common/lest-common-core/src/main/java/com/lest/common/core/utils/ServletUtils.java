package com.lest.common.core.utils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servlet 工具类
 *
 * @author yshan2028
 */
public class ServletUtils {

    public static ServletRequest getRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) getResponse();
    }

    public static HttpSession getSession() {
        return getHttpServletRequest().getSession();
    }

    public static ServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }

    public static String getParameter(String name) {
        return getHttpServletRequest().getParameter(name);
    }

    public static String getParameter(String name, String defaultValue) {
        String value = getHttpServletRequest().getParameter(name);
        return StringUtils.isNotEmpty(value) ? value : defaultValue;
    }

    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getHttpServletRequest().getParameter(name));
    }

    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getHttpServletRequest().getParameter(name), defaultValue);
    }

    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getHttpServletRequest().getParameter(name));
    }

    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getHttpServletRequest().getParameter(name), defaultValue);
    }

    public static String getHeader(String name) {
        return getHttpServletRequest().getHeader(name);
    }

    public static void renderString(HttpServletResponse response, String string) {
        renderString(response, string, "application/json", StandardCharsets.UTF_8.name());
    }

    public static void renderString(HttpServletResponse response, String string, String contentType) {
        renderString(response, string, contentType, StandardCharsets.UTF_8.name());
    }

    public static void renderString(HttpServletResponse response, String string, String contentType, String charset) {
        try {
            response.setContentType(contentType);
            response.setCharacterEncoding(charset);
            response.getWriter().write(string);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        }
        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
            return true;
        }
        String uri = request.getRequestURI();
        if (StringUtils.isNotBlank(uri) && (uri.endsWith(".json") || uri.endsWith(".xml"))) {
            return true;
        }
        String ajax = request.getParameter("__ajax");
        return StringUtils.isNotBlank(ajax) && "json".equals(ajax);
    }

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip.split(",")[0];
    }

    public static boolean isGetRequest(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    public static String urlEncode(String str) {
        try {
            return java.net.URLEncoder.encode(str, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return "";
        }
    }

    public static String urlDecode(String str) {
        try {
            return java.net.URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return "";
        }
    }
}
