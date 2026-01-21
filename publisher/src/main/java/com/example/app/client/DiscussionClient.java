package com.example.app.client;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.kafka.KafkaProducerService;
import com.example.app.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DiscussionClient {
    
    private final WebClient webClient;
    private final KafkaProducerService kafkaProducerService;
    private final RedisCacheService cacheService;
    
    @Value("${discussion.mode:kafka}") // По умолчанию используем Kafka
    private String mode;
    
    @Value("${cache.reactions.enabled:true}")
    private boolean cacheEnabled;
    
    public DiscussionClient(KafkaProducerService kafkaProducerService,
                           RedisCacheService cacheService) {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:24130/api/v1.0")
            .defaultHeader("Content-Type", "application/json")
            .build();
        this.kafkaProducerService = kafkaProducerService;
        this.cacheService = cacheService;
    }
    
    public List<ReactionResponseDTO> getReactionsByTweetId(Long tweetId) {
        // Проверяем кеш, если включено кеширование
        if (cacheEnabled) {
            List<ReactionResponseDTO> cachedReactions = getReactionsFromCache(tweetId);
            if (cachedReactions != null && !cachedReactions.isEmpty()) {
                System.out.println("Getting reactions from cache for tweet: " + tweetId);
                return cachedReactions;
            }
        }
        
        // Если нет в кеше, загружаем через REST
        try {
            List<ReactionResponseDTO> reactions = webClient.get()
                .uri("/reactions?tweetId={tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(ReactionResponseDTO.class)
                .collectList()
                .block();
            
            // Сохраняем в кеш
            if (cacheEnabled && reactions != null) {
                cacheReactions(tweetId, reactions);
            }
            
            return reactions != null ? reactions : List.of();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return List.of();
            }
            throw new RuntimeException("Error fetching reactions from discussion service", e);
        }
    }
    
    public ReactionResponseDTO createReaction(ReactionRequestDTO request) {
        ReactionResponseDTO response;
        
        if ("kafka".equalsIgnoreCase(mode)) {
            // Асинхронное создание через Kafka
            response = kafkaProducerService.sendReactionForModeration(request);
        } else {
            // Синхронное создание через REST
            response = createReactionSync(request);
        }
        
        // Инвалидируем кеш реакций для этого твита
        if (cacheEnabled && response != null && response.getTweetId() != null) {
            invalidateReactionCache(response.getTweetId());
        }
        
        return response;
    }
    
    // Синхронное создание через REST
    private ReactionResponseDTO createReactionSync(ReactionRequestDTO request) {
        try {
            return webClient.post()
                .uri("/reactions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReactionResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to create reaction via REST. Error: " + e.getStatusCode(), e);
        }
    }
    
    public ReactionResponseDTO updateReaction(Long id, ReactionRequestDTO request) {
        ReactionResponseDTO response;
        
        if ("kafka".equalsIgnoreCase(mode)) {
            // Обновление через Kafka
            response = kafkaProducerService.sendReactionUpdate(id, request);
        } else {
            // Синхронное обновление через REST
            response = updateReactionSync(id, request);
        }
        
        // Инвалидируем кеш реакций для этого твита
        if (cacheEnabled && response != null && response.getTweetId() != null) {
            invalidateReactionCache(response.getTweetId());
        }
        
        return response;
    }
    
    private ReactionResponseDTO updateReactionSync(Long id, ReactionRequestDTO request) {
        try {
            return webClient.put()
                .uri("/reactions/{id}?tweetId={tweetId}&country={country}", 
                    id, request.getTweetId(), request.getCountry())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ReactionResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to update reaction via REST. Error: " + e.getStatusCode(), e);
        }
    }
    
    public void deleteReaction(Long id, Long tweetId) {
        if ("kafka".equalsIgnoreCase(mode)) {
            // Удаление через Kafka
            kafkaProducerService.sendReactionDeletion(id, tweetId);
        } else {
            // Синхронное удаление через REST
            deleteReactionSync(id, tweetId);
        }
        
        // Инвалидируем кеш реакций для этого твита
        if (cacheEnabled) {
            invalidateReactionCache(tweetId);
        }
    }
    
    private void deleteReactionSync(Long id, Long tweetId) {
        try {
            webClient.delete()
                .uri("/reactions/{id}?tweetId={tweetId}", id, tweetId)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to delete reaction via REST. Error: " + e.getStatusCode(), e);
        }
    }
    
    public ReactionResponseDTO getReactionById(Long id, Long tweetId) {
        // Получение по ID всегда синхронное
        try {
            return webClient.get()
                .uri("/reactions/{id}?tweetId={tweetId}", id, tweetId)
                .retrieve()
                .bodyToMono(ReactionResponseDTO.class)
                .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to get reaction via REST. Error: " + e.getStatusCode(), e);
        }
    }
    
    // Получить только APPROVED реакции
    public List<ReactionResponseDTO> getApprovedReactionsByTweetId(Long tweetId) {
        // Проверяем кеш для approved реакций
        if (cacheEnabled) {
            List<ReactionResponseDTO> cachedApproved = getApprovedReactionsFromCache(tweetId);
            if (cachedApproved != null && !cachedApproved.isEmpty()) {
                System.out.println("Getting approved reactions from cache for tweet: " + tweetId);
                return cachedApproved;
            }
        }
        
        try {
            List<ReactionResponseDTO> approvedReactions = webClient.get()
                .uri("/reactions/tweet/{tweetId}?state=APPROVE", tweetId)
                .retrieve()
                .bodyToFlux(ReactionResponseDTO.class)
                .collectList()
                .block();
            
            // Сохраняем approved реакции в отдельный кеш
            if (cacheEnabled && approvedReactions != null) {
                cacheApprovedReactions(tweetId, approvedReactions);
            }
            
            return approvedReactions != null ? approvedReactions : List.of();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return List.of();
            }
            throw new RuntimeException("Error fetching approved reactions", e);
        }
    }
    
    // Удалить все реакции для твита
    public void deleteReactionsByTweetId(Long tweetId) {
        try {
            webClient.delete()
                .uri("/reactions/tweet/{tweetId}", tweetId)
                .retrieve()
                .toBodilessEntity()
                .block();
            
            // Инвалидируем кеш
            if (cacheEnabled) {
                invalidateReactionCache(tweetId);
            }
            
        } catch (WebClientResponseException e) {
            System.err.println("Failed to delete reactions for tweet " + tweetId + ": " + e.getMessage());
        }
    }
    
    // === CACHE METHODS ===
    
    private List<ReactionResponseDTO> getReactionsFromCache(Long tweetId) {
        try {
            return (List<ReactionResponseDTO>) cacheService.getReactionsFromCache(tweetId);
        } catch (Exception e) {
            System.err.println("Error reading from cache: " + e.getMessage());
            return null;
        }
    }
    
    private List<ReactionResponseDTO> getApprovedReactionsFromCache(Long tweetId) {
        try {
            return (List<ReactionResponseDTO>) cacheService.getApprovedReactionsFromCache(tweetId);
        } catch (Exception e) {
            System.err.println("Error reading approved from cache: " + e.getMessage());
            return null;
        }
    }
    
    private void cacheReactions(Long tweetId, List<ReactionResponseDTO> reactions) {
        try {
            cacheService.cacheReactions(tweetId, reactions);
            System.out.println("Cached reactions for tweet: " + tweetId);
        } catch (Exception e) {
            System.err.println("Error caching reactions: " + e.getMessage());
        }
    }
    
    private void cacheApprovedReactions(Long tweetId, List<ReactionResponseDTO> reactions) {
        try {
            cacheService.cacheApprovedReactions(tweetId, reactions);
            System.out.println("Cached approved reactions for tweet: " + tweetId);
        } catch (Exception e) {
            System.err.println("Error caching approved reactions: " + e.getMessage());
        }
    }
    
    private void invalidateReactionCache(Long tweetId) {
        try {
            cacheService.evictTweetReactionsCache(tweetId);
            cacheService.evictApprovedReactionsCache(tweetId);
            System.out.println("Invalidated cache for tweet: " + tweetId);
        } catch (Exception e) {
            System.err.println("Error invalidating cache: " + e.getMessage());
        }
    }
    
    // Метод для принудительной инвалидации кеша (например, из Kafka listener)
    public void invalidateCacheForTweet(Long tweetId) {
        invalidateReactionCache(tweetId);
    }
}