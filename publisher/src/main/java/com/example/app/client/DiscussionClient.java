package com.example.app.client;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.kafka.KafkaProducerService;
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
    
    @Value("${discussion.mode:kafka}") // По умолчанию используем Kafka
    private String mode;
    
    public DiscussionClient(KafkaProducerService kafkaProducerService) {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:24130/api/v1.0")
            .defaultHeader("Content-Type", "application/json")
            .build();
        this.kafkaProducerService = kafkaProducerService;
    }
    
    public List<ReactionResponseDTO> getReactionsByTweetId(Long tweetId) {
        // Получение реакций всегда через REST (синхронное)
        try {
            return webClient.get()
                .uri("/reactions?tweetId={tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(ReactionResponseDTO.class)
                .collectList()
                .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return List.of();
            }
            throw new RuntimeException("Error fetching reactions from discussion service", e);
        }
    }
    
    public ReactionResponseDTO createReaction(ReactionRequestDTO request) {
        if ("kafka".equalsIgnoreCase(mode)) {
            // Асинхронное создание через Kafka
            return kafkaProducerService.sendReactionForModeration(request);
        } else {
            // Синхронное создание через REST (для обратной совместимости)
            return createReactionSync(request);
        }
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
        if ("kafka".equalsIgnoreCase(mode)) {
            // Обновление через Kafka
            return kafkaProducerService.sendReactionUpdate(id, request);
        } else {
            // Синхронное обновление через REST
            return updateReactionSync(id, request);
        }
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
        try {
            return webClient.get()
                .uri("/reactions/tweet/{tweetId}?state=APPROVE", tweetId)
                .retrieve()
                .bodyToFlux(ReactionResponseDTO.class)
                .collectList()
                .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return List.of();
            }
            throw new RuntimeException("Error fetching approved reactions", e);
        }
    }
}