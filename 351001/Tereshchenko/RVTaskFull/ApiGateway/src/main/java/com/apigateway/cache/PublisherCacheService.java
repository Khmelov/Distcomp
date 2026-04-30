package com.apigateway.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PublisherCacheService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final boolean enabled;
    private final long ttlSeconds;
    private final String keyPrefix;

    public PublisherCacheService(
            ReactiveStringRedisTemplate redisTemplate,
            @Value("${app.cache.enabled:true}") boolean enabled,
            @Value("${app.cache.ttl-seconds:60}") long ttlSeconds,
            @Value("${app.cache.key-prefix:rv:publisher:cache:}") String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.enabled = enabled;
        this.ttlSeconds = ttlSeconds;
        this.keyPrefix = keyPrefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String buildKey(String method, String path, String query) {
        StringBuilder key = new StringBuilder(keyPrefix)
                .append(method)
                .append(':')
                .append(path);

        if (query != null && !query.isBlank()) {
            key.append('?').append(query);
        }

        return key.toString();
    }

    public Mono<String> get(String key) {
        if (!enabled) {
            return Mono.empty();
        }
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> put(String key, String value) {
        if (!enabled || value == null) {
            return Mono.just(false);
        }
        return redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    public Mono<Long> evictAll() {
        if (!enabled) {
            return Mono.just(0L);
        }
        return redisTemplate.keys(keyPrefix + "*")
                .collectList()
                .flatMap(keys -> keys.isEmpty()
                        ? Mono.just(0L)
                        : redisTemplate.delete(Flux.fromIterable(keys)));
    }
}
