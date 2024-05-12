package com.forzlp.project.common.biz;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.stereotype.Component;

/**
 * 用户上下文
 */
@Component
public final class UserContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    // public static void setToken(String token) {
    //     USER_THREAD_LOCAL.set(token);
    // }

    public static String getUsername() {
        return USER_THREAD_LOCAL.get().getUsername();
    }

    public static String getToken() {
        return USER_THREAD_LOCAL.get().getToken();
    }

    public static Long getUserId() {
        return USER_THREAD_LOCAL.get().getUserId();
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}