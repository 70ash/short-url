package com.example.demo.config;


import com.example.demo.common.biz.UserTransmitFilter;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置自动装配
 */
@Configuration
@AllArgsConstructor
public class UserConfiguration {
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用户信息传递过滤器
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter(stringRedisTemplate));
        registration.addUrlPatterns("/short-link/group/*");
        registration.addUrlPatterns("/short-link/user/logout");
        registration.addUrlPatterns("/short-link/user");
        // registration.addUrlPatterns("/*");
        return registration;
    }

    // @Bean
    // public FilterRegistrationBean<CORSFilter> globalUserCorsFilter() {
    //     FilterRegistrationBean<CORSFilter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(new CORSFilter());
    //     registration.addUrlPatterns("/*");
    //     return registration;
    // }
}
