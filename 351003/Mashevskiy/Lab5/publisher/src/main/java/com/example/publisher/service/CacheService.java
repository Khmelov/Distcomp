package com.example.publisher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    public CacheService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private static final long TTL = 10; // 10 минут

    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value, TTL, TimeUnit.MINUTES);
            System.out.println("Cached: " + key);
        } catch (Exception e) {
            System.err.println("Redis put error for key " + key + ": " + e.getMessage());
        }
    }

    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                System.out.println("Cache hit: " + key);
            } else {
                System.out.println("Cache miss: " + key);
            }
            return value;
        } catch (Exception e) {
            System.err.println("Redis get error for key " + key + ": " + e.getMessage());
            return null;
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            System.out.println("Cache evicted: " + key);
        } catch (Exception e) {
            System.err.println("Redis evict error for key " + key + ": " + e.getMessage());
        }
    }

    public void evictByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println("Cache evicted by pattern: " + pattern);
            }
        } catch (Exception e) {
            System.err.println("Redis evictByPattern error: " + e.getMessage());
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }
}