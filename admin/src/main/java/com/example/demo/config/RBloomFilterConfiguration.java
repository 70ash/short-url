package com.example.demo.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(10000000, 0.0001);
        return cachePenetrationBloomFilter;
    }

    /**
     * 分组标识布隆过滤器
     */
    @Bean
    public RBloomFilter<String> groupRegisterCacheBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("groupRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(10000000, 0.0001);
        return cachePenetrationBloomFilter;
    }

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortUrlCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(10000000, 0.0001);
        return cachePenetrationBloomFilter;
    }

}