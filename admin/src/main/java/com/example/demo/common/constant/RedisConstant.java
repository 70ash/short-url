package com.example.demo.common.constant;

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
}
