
package com.example.task330.publisher.client;

import com.example.task330.publisher.dto.ReactionRequestTo;
import com.example.task330.publisher.dto.ReactionResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

// Клиент для взаимодействия Publisher с микросервисом Discussion.
@Component
public class DiscussionClient {

    private final RestClient restClient;

    // Создание HTTP-клиента с адресом микросервиса Discussion.
    public DiscussionClient(
            @Value("${discussion.service.url}") String discussionServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(discussionServiceUrl)
                .build();
    }

    // Получение списка всех реакций.
    public List<ReactionResponseTo> findAll() {
        return restClient.get()
                .uri("/api/v1.0/reactions")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // Получение реакции по идентификатору.
    public ReactionResponseTo findById(Long id) {
        return restClient.get()
                .uri("/api/v1.0/reactions/{id}", id)
                .retrieve()
                .body(ReactionResponseTo.class);
    }

    // Создание новой реакции.
    public ReactionResponseTo create(ReactionRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/reactions")
                .body(request)
                .retrieve()
                .body(ReactionResponseTo.class);
    }

    // Обновление существующей реакции.
    public ReactionResponseTo update(Long id, ReactionRequestTo request) {
        return restClient.put()
                .uri("/api/v1.0/reactions/{id}", id)
                .body(request)
                .retrieve()
                .body(ReactionResponseTo.class);
    }

    // Удаление реакции по идентификатору.
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/api/v1.0/reactions/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    // Удаление всех реакций, связанных с указанной темой.
    public void deleteReactionsByTopicId(Long topicId) {
        restClient.delete()
                .uri("/api/v1.0/reactions/topic/{topicId}", topicId)
                .retrieve()
                .toBodilessEntity();
    }
}
