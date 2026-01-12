package com.task.rest.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AUTHOR_CACHE_PREFIX = "author:";
    private static final String TWEET_CACHE_PREFIX = "tweet:";
    private static final String MARK_CACHE_PREFIX = "mark:";
    private static final String NOTICE_CACHE_PREFIX = "notice:";

    private static final long DEFAULT_TTL_SECONDS = 60;

    // ==================== Generic Methods ====================

    public <T> void put(String key, T value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to cache value for key: {}: {}", key, e.getMessage());
        }
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Cache hit for key: {}", key);
                T result = objectMapper.convertValue(value, clazz);
                return Optional.of(result);
            }
            log.debug("Cache miss for key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to get value from cache for key: {}: {}", key, e.getMessage());
        }
        return Optional.empty();
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted cache for key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to delete cache for key: {}: {}", key, e.getMessage());
        }
    }

    public void deleteByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.warn("Failed to delete cache by pattern: {}: {}", pattern, e.getMessage());
        }
    }

    // ==================== Author Cache ====================

    public String getAuthorCacheKey(Long id) {
        return AUTHOR_CACHE_PREFIX + id;
    }

    public <T> void cacheAuthor(Long id, T author) {
        put(getAuthorCacheKey(id), author, DEFAULT_TTL_SECONDS);
    }

    public <T> Optional<T> getAuthorFromCache(Long id, Class<T> clazz) {
        return get(getAuthorCacheKey(id), clazz);
    }

    public void evictAuthor(Long id) {
        delete(getAuthorCacheKey(id));
    }

    // ==================== Tweet Cache ====================

    public String getTweetCacheKey(Long id) {
        return TWEET_CACHE_PREFIX + id;
    }

    public <T> void cacheTweet(Long id, T tweet) {
        put(getTweetCacheKey(id), tweet, DEFAULT_TTL_SECONDS);
    }

    public <T> Optional<T> getTweetFromCache(Long id, Class<T> clazz) {
        return get(getTweetCacheKey(id), clazz);
    }

    public void evictTweet(Long id) {
        delete(getTweetCacheKey(id));
    }

    // ==================== Mark Cache ====================

    public String getMarkCacheKey(Long id) {
        return MARK_CACHE_PREFIX + id;
    }

    public <T> void cacheMark(Long id, T mark) {
        put(getMarkCacheKey(id), mark, DEFAULT_TTL_SECONDS);
    }

    public <T> Optional<T> getMarkFromCache(Long id, Class<T> clazz) {
        return get(getMarkCacheKey(id), clazz);
    }

    public void evictMark(Long id) {
        delete(getMarkCacheKey(id));
    }

    // ==================== Notice Cache ====================

    public String getNoticeCacheKey(Long id) {
        return NOTICE_CACHE_PREFIX + id;
    }

    public <T> void cacheNotice(Long id, T notice) {
        put(getNoticeCacheKey(id), notice, DEFAULT_TTL_SECONDS);
    }

    public <T> Optional<T> getNoticeFromCache(Long id, Class<T> clazz) {
        return get(getNoticeCacheKey(id), clazz);
    }

    public void evictNotice(Long id) {
        delete(getNoticeCacheKey(id));
    }
}