package com.example.app.client;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DiscussionClient {
    
    private final WebClient webClient;
    
    public DiscussionClient() {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:24130/api/v1.0")
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    public List<ReactionResponseDTO> getReactionsByTweetId(Long tweetId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/reactions")
                    .queryParam("tweetId", tweetId)
                    .build())
                .retrieve()
                .bodyToFlux(ReactionResponseDTO.class)
                .collectList()
                .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return List.of(); // Возвращаем пустой список если нет реакций
            }
            throw new RuntimeException("Error fetching reactions from discussion service", e);
        }
    }
    
    public ReactionResponseDTO createReaction(ReactionRequestDTO request) {
        return webClient.post()
            .uri("/reactions")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> 
                Mono.error(new RuntimeException("Client error when creating reaction"))
            )
            .onStatus(HttpStatusCode::is5xxServerError, response -> 
                Mono.error(new RuntimeException("Discussion service error when creating reaction"))
            )
            .bodyToMono(ReactionResponseDTO.class)
            .block();
    }
    
    public ReactionResponseDTO updateReaction(Long id, ReactionRequestDTO request) {
        return webClient.put()
            .uri(uriBuilder -> uriBuilder
                .path("/reactions/{id}")
                .queryParam("tweetId", request.getTweetId())
                .queryParam("country", request.getCountry())
                .build(id))
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ReactionResponseDTO.class)
            .block();
    }
    
    public void deleteReaction(Long id, Long tweetId) {
        webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/reactions/{id}")
                .queryParam("tweetId", tweetId)
                .build(id))
            .retrieve()
            .toBodilessEntity()
            .block();
    }
    
    public ReactionResponseDTO getReactionById(Long id, Long tweetId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/reactions/{id}")
                .queryParam("tweetId", tweetId)
                .build(id))
            .retrieve()
            .bodyToMono(ReactionResponseDTO.class)
            .block();
    }
}