package com.forzlp.project.common.constant;

/**
 * Author 70ash
 * Date 2024/4/22 下午9:59
 * Description:
 */
public class LinkRedisConstant {
    public static final String LINK_GOTO_KEY = "short-link:goto:%s";
    public static final String LOCK_LINK_GOTO_KEY = "short-link:lock:goto:%s";
    public static final String LINK_GOTO_IS_NULL_KEY = "short-link:null:goto:%s";

    public static final Long LINK_GOTO_NULL_TIME_KEY = 30L;
}
