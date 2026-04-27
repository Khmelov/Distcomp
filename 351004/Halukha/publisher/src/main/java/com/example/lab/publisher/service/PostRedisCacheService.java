package com.example.lab.publisher.service;

import java.time.Duration;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PostRedisCacheService {

    private static final Duration POST_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public PostRedisCacheService(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    private String keyById(Long id) {
        return "posts:" + id;
    }

    public Object get(Long id) {
        try {
            String json = redis.opsForValue().get(keyById(id));
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, Object.class);
        } catch (DataAccessException e) {
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void put(Long id, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redis.opsForValue().set(keyById(id), json, POST_TTL);
        } catch (DataAccessException e) {
            // skip cache on redis issues
        } catch (JsonProcessingException e) {
            // skip cache on serialization issues
        }
    }

    public void evict(Long id) {
        try {
            redis.delete(keyById(id));
        } catch (DataAccessException e) {
            // skip
        }
    }
}

