package by.shaminko.distcomp.services;

import by.shaminko.distcomp.dto.ReactionMapper;
import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;
import by.shaminko.distcomp.dto.*;
import by.shaminko.distcomp.kafka.KafkaClient;
import by.shaminko.distcomp.kafka.MessageData;
import by.shaminko.distcomp.repositories.TweetRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ReactionService {
    @Qualifier("reactionMapper")
    public final ReactionMapper mapper;
    private final KafkaClient kafkaClient;
    private final WebClient webClient;
    private final TweetRepository tweetRepository;

    @Value("${app.discussion.url}")
    private String discussionUrl;

    public List<ReactionResponseTo> getAll() {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.GET_ALL)).responseTOs();
        } catch (Exception e) {
            try {
                return webClient.get()
                        .uri(discussionUrl + "/api/v1.0/messages")
                        .retrieve()
                        .bodyToFlux(ReactionResponseTo.class).collectList().block();
            } catch (WebClientRequestException connectionError) {
                return Collections.emptyList();
            }
        }
    }

    @Cacheable(value = "reactions", key = "#id")
    public ReactionResponseTo getById(Long id) {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.GET_BY_ID, id)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.get()
                        .uri(discussionUrl + "/api/v1.0/messages/{id}", id)
                        .retrieve()
                        .bodyToMono(ReactionResponseTo.class).block();
            } catch (WebClientRequestException connectionError) {
                ReactionResponseTo fallback = new ReactionResponseTo();
                fallback.setId(id);
                return fallback;
            }
        }
    }

    @CachePut(value = "reactions", key = "#req.id")
    public ReactionResponseTo create(ReactionRequestTo req) {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.CREATE, req)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.post()
                        .uri(discussionUrl + "/api/v1.0/messages")
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(ReactionResponseTo.class).block();
            } catch (WebClientRequestException connectionError) {
                ReactionResponseTo fallbackResponse = new ReactionResponseTo();
                fallbackResponse.setId(System.currentTimeMillis());
                fallbackResponse.setArticleId(req.getArticleId());
                fallbackResponse.setContent(req.getContent());
                fallbackResponse.setState("PENDING");
                return fallbackResponse;
            }
        }
    }

    @CachePut(value = "reactions", key = "#req.id")
    public ReactionResponseTo update(ReactionRequestTo req) {
        req.setState("PENDING");
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.UPDATE, req)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.put()
                        .uri(discussionUrl + "/api/v1.0/messages")
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(ReactionResponseTo.class).block();
            } catch (WebClientRequestException connectionError) {
                ReactionResponseTo fallbackResponse = new ReactionResponseTo();
                fallbackResponse.setId(req.getId());
                fallbackResponse.setArticleId(req.getArticleId());
                fallbackResponse.setContent(req.getContent());
                fallbackResponse.setState("PENDING");
                return fallbackResponse;
            }
        }
    }

    @CacheEvict(value = "reactions", key = "#id")
    public void delete(Long id) {
        try {
            kafkaClient.send(new MessageData(MessageData.Operation.DELETE_BY_ID, id));
        } catch (Exception e) {
            try {
                webClient.delete()
                        .uri(discussionUrl + "/api/v1.0/messages/{id}", id)
                        .retrieve()
                        .bodyToMono(Void.class).block();
            } catch (WebClientRequestException connectionError) {
            }
        }
    }
}