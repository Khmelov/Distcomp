package by.bsuir.task310.service;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import by.bsuir.task310.exception.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ReactionService {

    private final KafkaTemplate<String, ReactionRequestTo> kafkaTemplate;
    private final WebClient webClient;

    public ReactionService(KafkaTemplate<String, ReactionRequestTo> kafkaTemplate, WebClient webClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.webClient = webClient;
    }

    public ReactionResponseTo create(ReactionRequestTo requestTo) {
        kafkaTemplate.send("InTopic", requestTo);

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 1000) {
            List<ReactionResponseTo> reactions = getAll();

            for (int i = reactions.size() - 1; i >= 0; i--) {
                ReactionResponseTo reaction = reactions.get(i);
                if (reaction.getTopicId().equals(requestTo.getTopicId())
                        && reaction.getContent().equals(requestTo.getContent())) {
                    return reaction;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new RuntimeException("Kafka response timeout");
    }

    public List<ReactionResponseTo> getAll() {
        return webClient.get()
                .uri("/api/v1.0/reactions")
                .retrieve()
                .bodyToFlux(ReactionResponseTo.class)
                .collectList()
                .block();
    }

    @Cacheable(value = "reactions", key = "#id")
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

    @CachePut(value = "reactions", key = "#requestTo.id")
    public ReactionResponseTo update(ReactionRequestTo requestTo) {
        return webClient.put()
                .uri("/api/v1.0/reactions")
                .bodyValue(requestTo)
                .retrieve()
                .bodyToMono(ReactionResponseTo.class)
                .block();
    }

    @CacheEvict(value = "reactions", key = "#id")
    public void delete(Long id) {
        webClient.delete()
                .uri("/api/v1.0/reactions/" + id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}