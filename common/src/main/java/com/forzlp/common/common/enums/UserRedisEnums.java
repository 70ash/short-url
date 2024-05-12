package com.forzlp.common.common.enums;

import lombok.AllArgsConstructor;



/**
 * Author 70ash
 * Date 2024/1/26 23:47
 * Description:
 */

@AllArgsConstructor
public enum UserRedisEnums {
    /**
     * 短链接用户注册常量
     */
    LOCK_USER_REGISTER("short-link:user_register:"),
    USER_LOGIN_KEY("login:");

    private final String value;
}
