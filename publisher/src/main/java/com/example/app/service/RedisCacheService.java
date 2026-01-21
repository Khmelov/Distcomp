package com.example.app.service;

import com.example.app.dto.cache.CachedAuthorDTO;
import com.example.app.dto.cache.CachedTweetDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // === AUTHOR CACHE ===
    
    @Cacheable(value = "authors", key = "#id")
    public CachedAuthorDTO getAuthorFromCache(Long id) {
        // Этот метод вызывается только если данных нет в кеше
        // Реальная загрузка из БД будет в AuthorService
        return null;
    }
    
    @CacheEvict(value = "authors", key = "#id")
    public void evictAuthorCache(Long id) {
        // Автоматически вызывается аннотацией
    }
    
    @CacheEvict(value = "authors", allEntries = true)
    public void evictAllAuthorsCache() {
        // Автоматически вызывается аннотацией
    }
    
    // === TWEET CACHE ===
    
    @Cacheable(value = "tweets", key = "#id")
    public CachedTweetDTO getTweetFromCache(Long id) {
        return null; // Загрузка из БД будет в TweetService
    }
    
    @Cacheable(value = "tweets", key = "#id + '_approved'")
    public CachedTweetDTO getTweetWithApprovedReactionsFromCache(Long id) {
        return null;
    }
    
    @CacheEvict(value = "tweets", key = "#id")
    public void evictTweetCache(Long id) {
        // Также нужно очистить кеш с реакциями
        evictTweetReactionsCache(id);
        evictTweetApprovedCache(id);
    }
    
    @CacheEvict(value = "tweets", key = "#id + '_approved'")
    public void evictTweetApprovedCache(Long id) {
        // Автоматически вызывается аннотацией
    }
    
    @CacheEvict(value = "tweets", allEntries = true)
    public void evictAllTweetsCache() {
        evictAllReactionsCache();
    }
    
    // === REACTIONS CACHE ===
    
    @Cacheable(value = "reactions", key = "#tweetId")
    public List<Object> getReactionsFromCache(Long tweetId) {
        return null;
    }
    
    @Cacheable(value = "reactions", key = "#tweetId + '_approved'")
    public List<Object> getApprovedReactionsFromCache(Long tweetId) {
        return null;
    }
    
    @CacheEvict(value = "reactions", key = "#tweetId")
    public void evictTweetReactionsCache(Long tweetId) {
        // Также очищаем approved версию
        evictApprovedReactionsCache(tweetId);
    }
    
    @CacheEvict(value = "reactions", key = "#tweetId + '_approved'")
    public void evictApprovedReactionsCache(Long tweetId) {
        // Автоматически вызывается аннотацией
    }
    
    @CacheEvict(value = "reactions", allEntries = true)
    public void evictAllReactionsCache() {
        // Автоматически вызывается аннотацией
    }
    
    // === MANUAL CACHE METHODS ===
    
    public void cacheAuthor(Long id, CachedAuthorDTO author) {
        redisTemplate.opsForValue().set("author:" + id, author, 10, TimeUnit.MINUTES);
    }
    
    public CachedAuthorDTO getCachedAuthor(Long id) {
        return (CachedAuthorDTO) redisTemplate.opsForValue().get("author:" + id);
    }
    
    public void cacheTweet(Long id, CachedTweetDTO tweet) {
        redisTemplate.opsForValue().set("tweet:" + id, tweet, 10, TimeUnit.MINUTES);
        // Также кешируем отдельно реакции
        if (tweet.getReactions() != null) {
            redisTemplate.opsForValue().set(
                "tweet:" + id + ":reactions", 
                tweet.getReactions(), 
                5, 
                TimeUnit.MINUTES
            );
        }
    }
    
    public CachedTweetDTO getCachedTweet(Long id) {
        return (CachedTweetDTO) redisTemplate.opsForValue().get("tweet:" + id);
    }
    
    // Инвалидация по паттерну
    public void evictPattern(String pattern) {
        redisTemplate.keys(pattern).forEach(key -> redisTemplate.delete(key));
    }
}