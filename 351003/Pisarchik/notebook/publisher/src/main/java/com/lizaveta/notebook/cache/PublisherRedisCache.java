package com.lizaveta.notebook.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class PublisherRedisCache {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public PublisherRedisCache(
            @Qualifier("notebookRedisTemplate") final RedisTemplate<String, String> redisTemplate,
            final ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> get(final String key, final Class<T> type) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception ex) {
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    public <T> Optional<T> get(final String key, final TypeReference<T> typeRef) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, typeRef));
        } catch (Exception ex) {
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    public void put(final String key, final Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize cache value for key: " + key, ex);
        }
    }

    public void evictKey(final String key) {
        redisTemplate.delete(key);
    }

    public void evictKeyPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
