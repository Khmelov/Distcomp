package com.example.publisher.service;

import com.example.publisher.dto.ReactionRequestTo;
import com.example.publisher.dto.ReactionResponseTo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final WebClient discussionWebClient;

    public ReactionResponseTo create(ReactionRequestTo dto) {
        return discussionWebClient.post()
                .uri("/reactions")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    public List<ReactionResponseTo> getAll() {
        return discussionWebClient.get()
                .uri("/reactions")
                .retrieve()
                .bodyToFlux(ReactionResponseTo.class)
                .collectList()
                .block();
    }

    public ReactionResponseTo get(Long id) {
        return discussionWebClient.get()
                .uri("/reactions/{id}", id)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    public ReactionResponseTo update(Long id, ReactionRequestTo dto) {
        return discussionWebClient.put()
                .uri("/reactions/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    public void delete(Long id) {
        discussionWebClient.delete()
                .uri("/reactions/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}