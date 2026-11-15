package com.example.task330.publisher.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

// Сервис обеспечивает сохранение, получение и удаление данных в Redis.
@Service
public class RedisCacheService {

    // Время хранения кешированных данных — 10 минут.
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Spring передаёт необходимые компоненты через конструктор.
    public RedisCacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Сохранение объекта в Redis в формате JSON.
    public void save(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to save data to Redis: " + key,
                    exception
            );
        }
    }

    // Получение объекта из Redis с преобразованием JSON в указанный тип.
    public <T> Optional<T> get(String key, Class<T> type) {
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to read data from Redis: " + key,
                    exception
            );
        }
    }

    // Получение списка объектов из Redis.
    public <T> Optional<T> get(
            String key,
            TypeReference<T> typeReference) {

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    objectMapper.readValue(json, typeReference)
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to read data from Redis: " + key,
                    exception
            );
        }
    }

    // Удаление одного ключа из Redis.
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Удаление всех ключей, соответствующих указанному шаблону.
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
