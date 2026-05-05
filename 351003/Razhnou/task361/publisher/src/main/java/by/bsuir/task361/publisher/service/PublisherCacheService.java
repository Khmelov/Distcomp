package by.bsuir.task361.publisher.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class PublisherCacheService {
    private final CacheManager cacheManager;

    public PublisherCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <T> T get(String cacheName, Object key, Class<T> type) {
        Cache.ValueWrapper cachedValue = getCache(cacheName).get(key);
        if (cachedValue == null || cachedValue.get() == null) {
            return null;
        }
        Object value = cachedValue.get();
        if (!type.isInstance(value)) {
            throw new IllegalStateException("Unexpected cache value type for cache '" + cacheName + "'");
        }
        return type.cast(value);
    }

    public void put(String cacheName, Object key, Object value) {
        getCache(cacheName).put(key, value);
    }

    public void evict(String cacheName, Object key) {
        getCache(cacheName).evict(key);
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache '" + cacheName + "' is not configured");
        }
        return cache;
    }
}
