package com.lest.common.core.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络工具类
 *
 * @author yshan2028
 */
public class NetUtils {

    private static final String LOCALHOST = "127.0.0.1";
    private static final String UNKNOWN = "unknown";

    private NetUtils() {
    }

    /**
     * 获取服务器 IP 地址
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return LOCALHOST;
        }
    }

    /**
     * 获取服务器主机名
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return UNKNOWN;
        }
    }

    /**
     * 判断是否为内网 IP
     */
    public static boolean isInnerIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("172.")
                && innerIp172(ip)
                || ip.equals("127.0.0.1")
                || ip.equals(LOCALHOST);
    }

    private static boolean innerIp172(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        try {
            int second = Integer.parseInt(parts[1]);
            return second >= 16 && second <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断 IP 地址是否有效
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String regex = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        return ip.matches(regex);
    }

    /**
     * 标准化 URL（移除协议前缀等）
     */
    public static String normalizeUrl(String url) {
        return URLUtil.normalize(url);
    }

    /**
     * URL 编码
     */
    public static String urlEncode(String content) {
        return URLUtil.encode(content);
    }

    /**
     * URL 解码
     */
    public static String urlDecode(String content) {
        return URLUtil.decode(content);
    }

    /**
     * 生成雪花 ID
     */
    public static long snowflakeId() {
        return IdUtil.getSnowflakeNextId();
    }

    /**
     * 生成带前缀的雪花 ID 字符串
     */
    public static String snowflakeIdStr() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
