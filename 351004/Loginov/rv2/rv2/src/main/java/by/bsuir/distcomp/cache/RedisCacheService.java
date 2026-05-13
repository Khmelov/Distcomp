package by.bsuir.distcomp.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : objectMapper.convertValue(value, type);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public <T> List<T> getList(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (!(value instanceof List<?> values)) {
                return null;
            }
            return values.stream()
                    .map(item -> objectMapper.convertValue(item, type))
                    .toList();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value, TTL);
        } catch (RuntimeException ignored) {
            // Redis is an optimization. Storage remains the source of truth.
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RuntimeException ignored) {
            // Ignore cache outages.
        }
    }

    public void evictByPrefix(String prefix) {
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (RedisConnectionFailureException ignored) {
            // Ignore cache outages.
        } catch (RuntimeException ignored) {
            // Ignore cache outages.
        }
    }
}
