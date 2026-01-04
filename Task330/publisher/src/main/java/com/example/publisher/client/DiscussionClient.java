package com.example.publisher.client;

import com.example.publisher.dto.CommentRequestTo;
import com.example.publisher.dto.CommentResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class DiscussionClient {
    private final WebClient webClient;

    public DiscussionClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:24130/api/v1.0")
                .build();
    }

    public List<CommentResponseTo> getAllComments() {
        return webClient.get()
                .uri("/comments")
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public CommentResponseTo getCommentById(Long id) {
        return webClient.get()
                .uri("/comments/{id}", id)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public List<CommentResponseTo> getCommentsByStoryId(Long storyId) {
        return webClient.get()
                .uri("/comments/byStory/{storyId}", storyId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public CommentResponseTo createComment(CommentRequestTo request) {
        return webClient.post()
                .uri("/comments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public CommentResponseTo updateComment(CommentRequestTo request) {
        return webClient.put()
                .uri("/comments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public void deleteComment(Long id) {
        try {
            webClient.delete()
                    .uri("/comments/{id}", id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            // Проглатываем 404 от Cassandra, как в ТЗ
        }
    }
}