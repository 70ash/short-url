// package com.forzlp.project.config;
//
// // import com.forzlp.project.mq.consumer.ShortLinkStatsSaveConsumer;
// import lombok.AllArgsConstructor;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.connection.stream.Consumer;
// import org.springframework.data.redis.connection.stream.MapRecord;
// import org.springframework.data.redis.connection.stream.ReadOffset;
// import org.springframework.data.redis.connection.stream.StreamOffset;
// import org.springframework.data.redis.stream.StreamMessageListenerContainer;
//
// import java.time.Duration;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.LinkedBlockingQueue;
// import java.util.concurrent.ThreadPoolExecutor;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicInteger;
//
// import static com.forzlp.project.common.constant.RedisConstant.*;
//
// /**
//  * Author 70ash
//  * Date 2024/5/16 下午4:34
//  * Description: RedisStream 作消息队列
//  */
// @AllArgsConstructor
// public class RedisStreamConfiguration {
//
//     private final RedisConnectionFactory redisConnectionFactory;
//     private final ShortLinkStatsSaveConsumer shortLinkStatsSaveConsumer;
//
//     @Value("${spring.data.redis.channel-topic.short-link-stats}")
//     private String topic;
//     @Value("${spring.data.redis.channel-topic.short-link-stats-group}")
//     private String group;
//
//     @Bean
//     public ExecutorService asyncStreamConsumerExecutor() {
//         AtomicInteger index = new AtomicInteger();
//         // 获取当前机器可用的cpu核数
//         int processors = Runtime.getRuntime().availableProcessors();
//         return new ThreadPoolExecutor(
//                 processors,
//                 processors + processors >> 1,
//                 SHORT_LINK_STREAM_TIME,
//                 TimeUnit.SECONDS,
//                 new LinkedBlockingQueue<>(),
//                 runnable -> {
//                     Thread thread = new Thread(runnable);
//                     thread.setName("stream_consumer_short-link_stats_" + index.incrementAndGet());
//                     thread.setDaemon(true);
//                     return thread;
//                 }
//         );
//     }
//
//     @Bean(initMethod = "start", destroyMethod = "stop")
//     public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(ExecutorService asyncStreamConsumer) {
//         StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
//                 StreamMessageListenerContainer.StreamMessageListenerContainerOptions
//                         .builder()
//                         // 一次最多获取多少条消息
//                         .batchSize(10)
//                         // 执行从 Stream 拉取到消息的任务流程
//                         .executor(asyncStreamConsumer)
//                         // 如果没有拉取到消息，需要阻塞的时间。不能大于 ${spring.data.redis.timeout}，否则会超时
//                         .pollTimeout(Duration.ofSeconds(3))
//                         .build();
//         StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer =
//                 StreamMessageListenerContainer.create(redisConnectionFactory, options);
//         streamMessageListenerContainer.receiveAutoAck(Consumer.from(SHORT_LINK_STATS_STREAM_GROUP_KEY, "stats-consumer"),
//                 StreamOffset.create(SHORT_LINK_STATS_STREAM_TOPIC_KEY, ReadOffset.lastConsumed()), shortLinkStatsSaveConsumer);
//         return streamMessageListenerContainer;
//     }
// }
