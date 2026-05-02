package com.example.task310.service;

import com.example.task310.dto.PostResponseTo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "OutTopic", groupId = "distcomp-group")
    public void listenOutTopic(PostResponseTo post) {
        String key = "post_cache::" + post.id();
        redisTemplate.opsForValue().set(key, post, Duration.ofMinutes(10));
    }
}