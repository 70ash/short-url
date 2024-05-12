package com.example.demo.common.constant;

/**
 * Author 70ash
 * Date 2024/4/22 下午9:59
 * Description:
 */
public class LinkRedisConstant {
    /** 跳转key **/
    public static final String LINK_GOTO_KEY = "short-link:goto:%s";
    /** 分布式锁key **/
    public static final String LOCK_LINK_GOTO_KEY = "short-link:lock:goto:%s";
    /** 缓存空key **/
    public static final String LINK_GOTO_IS_NULL_KEY = "short-link:null:goto:%s";
    /** uv统计key **/
    public static final String LINK_UV_KEY = "short-link:uv:%s";
    /** 短链接跳转key **/
    public static final String LINK_IP_KEY = "short-link:ip:%s";
    public static final Long LINK_GOTO_NULL_TIME_KEY = 30L;
}
