package com.aitor.publisher.redis;

import com.aitor.publisher.dto.MessageResponseTo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
class MyConfig {

    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisTemplate<String, MessageResponseTo> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MessageResponseTo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}