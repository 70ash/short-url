package com.forzlp.project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Author 70ash
 * Date 2024/5/14 下午8:04
 * Type:
 * Description:
 */
public class LinkStatsUtil {
    /**
     * 获取请求的操作系统
     * @param request
     * @return
     */
    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "Mac OS";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            return "iOS";
        } else if (userAgent.toLowerCase().contains("unix")) {
            return "Unix";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "Linux";
        } else {
            return "Unknown";
        }
    }
    /**
     * 获取用户访问浏览器
     *
     * @param request 请求
     * @return 访问浏览器
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("edg")) {
            return "Microsoft Edge";
        } else if (userAgent.toLowerCase().contains("chrome")) {
            return "Google Chrome";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.toLowerCase().contains("safari")) {
            return "Apple Safari";
        } else if (userAgent.toLowerCase().contains("opera")) {
            return "Opera";
        } else if (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }
}
