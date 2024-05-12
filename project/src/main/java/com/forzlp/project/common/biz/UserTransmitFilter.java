package com.forzlp.project.common.biz;

import cn.hutool.json.JSONUtil;
import com.example.demo.common.convention.excetion.ClientException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

import static com.example.demo.common.constant.RedisConstant.USER_TOKEN_KEY;
import static com.example.demo.common.convention.errorcode.BaseErrorCode.USER_NOT_LOGIN;

/**
 * 拦截器
 * 用户登录后，存入redis用户信息，并返回token，当用户发起请求时，携带token被过滤器所解析，封装进
 */
@AllArgsConstructor
public class UserTransmitFilter implements Filter {
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = httpServletRequest.getHeader("token");
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(USER_TOKEN_KEY + token))) {
            // 处理自定义异常，存入servletRequest之中
            servletRequest.setAttribute("filter.error", new ClientException(USER_NOT_LOGIN) {
            });
            // 转发请求
            servletRequest.getRequestDispatcher("/myError").forward(servletRequest,servletResponse);
        }
        try {
            String userJson = stringRedisTemplate.opsForValue().get(USER_TOKEN_KEY+token);
            UserInfoDTO userInfoDTO = JSONUtil.toBean(userJson, UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            UserContext.removeUser();
        }

    }
}