package com.distcomp.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Small cache-aside helper on top of Redis.
 *
 * <p>Redis is treated as an accelerator, not as a source of truth. Every
 * operation swallows connection errors and falls back to the database, so a
 * stopped Redis instance only makes the application slower.</p>
 */
@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private final StringRedisTemplate redis;

    private final ObjectMapper objectMapper;

    private final boolean enabled;

    private final Duration ttl;

    public CacheService(StringRedisTemplate redis,
                        ObjectMapper objectMapper,
                        @Value("${app.cache.enabled:true}") boolean enabled,
                        @Value("${app.cache.ttl-seconds:300}") long ttlSeconds) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public <T> T get(String key, TypeReference<T> type) {
        if (!enabled) {
            return null;
        }
        try {
            String value = redis.opsForValue().get(key);
            return value == null ? null : objectMapper.readValue(value, type);
        } catch (Exception exception) {
            log.debug("Redis read failed for {}: {}", key, exception.getMessage());
            return null;
        }
    }

    public void put(String key, Object value) {
        if (!enabled) {
            return;
        }
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(value), ttl);
        } catch (Exception exception) {
            log.debug("Redis write failed for {}: {}", key, exception.getMessage());
        }
    }

    public void evict(String key) {
        if (!enabled) {
            return;
        }
        try {
            redis.delete(key);
        } catch (Exception exception) {
            log.debug("Redis delete failed for {}: {}", key, exception.getMessage());
        }
    }

    /**
     * Removes every key that starts with the prefix.
     *
     * <p>{@code SCAN} is used instead of {@code KEYS} so that a large cache does
     * not block the Redis event loop.</p>
     */
    public void evictByPrefix(String prefix) {
        if (!enabled) {
            return;
        }
        try {
            ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(100).build();
            try (Cursor<String> cursor = redis.scan(options)) {
                while (cursor.hasNext()) {
                    redis.delete(cursor.next());
                }
            }
        } catch (Exception exception) {
            log.debug("Redis prefix delete failed for {}: {}", prefix, exception.getMessage());
        }
    }

    /** Exposed for diagnostics and tests. */
    public boolean ping() {
        if (!enabled) {
            return false;
        }
        try {
            RedisConnection connection = redis.getConnectionFactory().getConnection();
            try {
                return "PONG".equalsIgnoreCase(connection.ping());
            } finally {
                connection.close();
            }
        } catch (Exception exception) {
            log.debug("Redis ping failed: {}", exception.getMessage());
            return false;
        }
    }

    /** @return the value of a key as plain text, or {@code null} */
    public String raw(String key) {
        if (!enabled) {
            return null;
        }
        try {
            byte[] bytes = redis.getConnectionFactory().getConnection()
                    .stringCommands().get(key.getBytes(StandardCharsets.UTF_8));
            return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            return null;
        }
    }
}
