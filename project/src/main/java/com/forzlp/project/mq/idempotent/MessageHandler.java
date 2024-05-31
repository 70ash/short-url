// package com.forzlp.project.mq.idempotent;
//
// import lombok.AllArgsConstructor;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.stereotype.Component;
//
// import java.util.concurrent.TimeUnit;
//
// /**
//  * Author 70ash
//  * Date 2024/5/18 下午8:25
//  * Description: 消息幂等处理器
//  */
// @Component
// @AllArgsConstructor
// public class MessageHandler {
//     public final String MD_PREFIX = "short-link:idempotent";
//     public StringRedisTemplate stringRedisTemplate;
//
//     public boolean isMessageIdempotent(String msgId) {
//         String key = MD_PREFIX + msgId;
//         return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.MINUTES));
//     }
// }
