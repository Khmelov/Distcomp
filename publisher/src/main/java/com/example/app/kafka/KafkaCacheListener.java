package com.example.app.kafka;

import com.example.app.dto.kafka.KafkaReactionMessage;
import com.example.app.service.RedisCacheService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaCacheListener {
    
    private final RedisCacheService cacheService;
    
    public KafkaCacheListener(RedisCacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    @KafkaListener(
        topics = "${kafka.topics.out-topic}",
        groupId = "${spring.kafka.consumer.group-id}-cache"
    )
    public void listenForCacheInvalidation(KafkaReactionMessage message) {
        if (message.getTweetId() != null) {
            // Инвалидируем кеш реакций при любом изменении
            cacheService.evictTweetReactionsCache(message.getTweetId());
            
            // Если реакция прошла модерацию, инвалидируем также approved кеш
            if ("MODERATE".equals(message.getOperation())) {
                cacheService.evictApprovedReactionsCache(message.getTweetId());
                cacheService.evictTweetApprovedCache(message.getTweetId());
            }
            
            System.out.println("Cache invalidated for tweet: " + message.getTweetId());
        }
    }
}