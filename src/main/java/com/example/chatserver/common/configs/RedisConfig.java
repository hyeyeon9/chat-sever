package com.example.chatserver.common.configs;

import com.example.chatserver.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // 연결 기본 객체
    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);

        // Redis pub./sub 기능은 특정 데이터베이스에 의존적이지 않고, 전역적이라 세팅하나마다 큰 의미없다.
        // configuration.setDatabase(0); // 0 ~ 10개의 DB가 있다.

        return new LettuceConnectionFactory(configuration);
    }

    // publish 객체
    @Bean
    @Qualifier("chatPubSub")
    // 일반적으로는 RedisTemplate<key데이터타입, value데이터타입> 객체를 사용한다.
    // 우리는 메시지 저장 목적이 아니라 그냥 전파 목적이라 stringRedisTemplate을 사용했다.
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    // suscribe 객체
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
                                                                  MessageListenerAdapter messageListenerAdapter     ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        // 리슨한 후 메시지 처리 객체 호출
        // chat이라고 하는 채널에 메시지가 들어오면 받아내고, messageListenerAdapter에게 던지겠디.
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));
        return container;
    }

    // redis에서 수신된 메시지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
        // RedisPubSubService의 특정 메서드가 수신된 메시지를 처리할 수 있도록 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }

}
