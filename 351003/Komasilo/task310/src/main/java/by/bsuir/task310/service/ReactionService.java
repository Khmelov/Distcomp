package by.bsuir.task310.service;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import by.bsuir.task310.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ReactionService {

    private final WebClient webClient;

    public ReactionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ReactionResponseTo create(ReactionRequestTo requestTo) {
        return webClient.post()
                .uri("/api/v1.0/reactions")
                .bodyValue(requestTo)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    public List<ReactionResponseTo> getAll() {
        return webClient.get()
                .uri("/api/v1.0/reactions")
                .retrieve()
                .bodyToFlux(ReactionResponseTo.class)
                .collectList()
                .block();
    }

    public ReactionResponseTo getById(Long id) {
        try {
            return webClient.get()
                    .uri("/api/v1.0/reactions/" + id)
                    .retrieve()
                    .bodyToMono(ReactionResponseTo.class)
                    .block();
        } catch (Exception e) {
            throw new EntityNotFoundException("Reaction not found");
        }
    }

    public ReactionResponseTo update(ReactionRequestTo requestTo) {
        return webClient.put()
                .uri("/api/v1.0/reactions")
                .bodyValue(requestTo)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    public void delete(Long id) {
        webClient.delete()
                .uri("/api/v1.0/reactions/" + id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}