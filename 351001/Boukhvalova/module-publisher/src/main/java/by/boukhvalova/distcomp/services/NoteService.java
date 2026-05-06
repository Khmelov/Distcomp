package by.boukhvalova.distcomp.services;

import by.boukhvalova.distcomp.dto.NoteRequestTo;
import by.boukhvalova.distcomp.dto.NoteResponseTo;
import by.shaminko.distcomp.dto.*;
import by.shaminko.distcomp.kafka.KafkaClient;
import by.shaminko.distcomp.kafka.MessageData;
import by.boukhvalova.distcomp.repositories.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final KafkaClient kafkaClient;
    private final WebClient webClient;
    private final TweetRepository tweetRepository;

    @Value("${app.discussion.url}")
    private String discussionUrl;

    public List<NoteResponseTo> getAll() {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.GET_ALL)).responseTOs();
        } catch (Exception e) {
            try {
                return webClient.get()
                        .uri(discussionUrl + "/api/v1.0/notes")
                        .retrieve()
                        .bodyToFlux(NoteResponseTo.class).collectList().block();
            } catch (WebClientResponseException ex2) {
                throw new ResponseStatusException(ex2.getStatusCode(), ex2.getResponseBodyAsString());
            } catch (WebClientRequestException connectionError) {
                return Collections.emptyList();
            }
        }
    }

    @Cacheable(value = "notes", key = "#id")
    public NoteResponseTo getById(Long id) {
        try {
                return kafkaClient.send(new MessageData(MessageData.Operation.GET_BY_ID, id)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.get()
                        .uri(discussionUrl + "/api/v1.0/notes/{id}", id)
                        .retrieve()
                        .bodyToMono(NoteResponseTo.class).block();
            } catch (WebClientResponseException ex2) {
                throw new ResponseStatusException(ex2.getStatusCode(), ex2.getResponseBodyAsString());
            } catch (WebClientRequestException connectionError) {
                NoteResponseTo fallback = new NoteResponseTo();
                fallback.setId(id);
                fallback.setUserId(0);
                return fallback;
            }
        }
    }

    @CachePut(value = "notes", key = "#req.id")
    public NoteResponseTo create(NoteRequestTo req) {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.CREATE, req)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.post()
                        .uri(discussionUrl + "/api/v1.0/notes")
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(NoteResponseTo.class).block();
            } catch (WebClientResponseException ex2) {
                throw new ResponseStatusException(ex2.getStatusCode(), ex2.getResponseBodyAsString());
            } catch (WebClientRequestException connectionError) {
                NoteResponseTo fallbackResponse = new NoteResponseTo();
                fallbackResponse.setId(System.currentTimeMillis());
                fallbackResponse.setTweetId(req.getTweetId());
                fallbackResponse.setUserId(req.getUserId());
                fallbackResponse.setContent(req.getContent());
                fallbackResponse.setCountry("PENDING");
                return fallbackResponse;
            }
        }
    }

    @CachePut(value = "notes", key = "#req.id")
    public NoteResponseTo update(NoteRequestTo req) {
        req.setCountry("PENDING");
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.UPDATE, req)).responseTOs().get(0);
        } catch (Exception e) {
            try {
                return webClient.put()
                        .uri(discussionUrl + "/api/v1.0/notes")
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(NoteResponseTo.class).block();
            } catch (WebClientResponseException ex2) {
                throw new ResponseStatusException(ex2.getStatusCode(), ex2.getResponseBodyAsString());
            } catch (WebClientRequestException connectionError) {
                NoteResponseTo fallbackResponse = new NoteResponseTo();
                fallbackResponse.setId(req.getId());
                fallbackResponse.setTweetId(req.getTweetId());
                fallbackResponse.setUserId(req.getUserId());
                fallbackResponse.setContent(req.getContent());
                fallbackResponse.setCountry("PENDING");
                return fallbackResponse;
            }
        }
    }

    @CacheEvict(value = "notes", key = "#id")
    public void delete(Long id) {
        try {
            kafkaClient.send(new MessageData(MessageData.Operation.DELETE_BY_ID, id));
        } catch (Exception e) {
            try {
                webClient.delete()
                        .uri(discussionUrl + "/api/v1.0/notes/{id}", id)
                        .retrieve()
                        .bodyToMono(Void.class).block();
            } catch (WebClientResponseException ex2) {
                throw new ResponseStatusException(ex2.getStatusCode(), ex2.getResponseBodyAsString());
            } catch (WebClientRequestException connectionError) {
            }
        }
    }
}
