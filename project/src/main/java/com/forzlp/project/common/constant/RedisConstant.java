package com.forzlp.project.common.constant;

/**
 * Author 70ash
 * Date 2024/1/26 23:47
 * Description:
 */
public class RedisConstant {
    public static final String LOCK_USER_REGISTER = "short-link:user_register:";
    public static final String USER_LOGIN_KEY = "login:";
    public static final String USER_TOKEN_KEY = "login:token:";

    public static final Long USER_TOKEN_TIME = 30L;

    public static final Long SHORT_LINK_STREAM_TIME = 60L;

    /**
     * 短链接监控消息保存队列 Topic 缓存标识
     */
    public static final String SHORT_LINK_STATS_STREAM_TOPIC_KEY = "short-link:stats-stream";

    /**
     * 短链接监控消息保存队列 Group 缓存标识
     */
    public static final String SHORT_LINK_STATS_STREAM_GROUP_KEY = "short-link:stats-stream:only-group";

    /**
     * 短链接统计判断是否新用户缓存标识
     */
    public static final String SHORT_LINK_STATS_UV_KEY = "short-link:stats:uv:";

    /**
     * 短链接统计判断是否新 IP 缓存标识
     */
    public static final String SHORT_LINK_STATS_UIP_KEY = "short-link:stats:uip:";

}
