package com.aitor.publisher.redis;

import com.aitor.publisher.dto.MessageResponseTo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisTemplate<String, MessageResponseTo> redisTemplate;

    public void setValue(String key, MessageResponseTo value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public MessageResponseTo getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
