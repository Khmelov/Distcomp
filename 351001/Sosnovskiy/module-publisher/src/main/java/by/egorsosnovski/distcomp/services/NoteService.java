package by.egorsosnovski.distcomp.services;

import by.egorsosnovski.distcomp.dto.*;
import by.egorsosnovski.distcomp.kafka.KafkaClient;
import by.egorsosnovski.distcomp.kafka.MessageData;
import by.egorsosnovski.distcomp.repositories.TweetRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class NoteService {
    @Qualifier("noteMapper")
    public final NoteMapper mapper;
    private final KafkaClient kafkaClient;
    private final WebClient webClient;
    private final TweetRepository tweetRepository;
    public List<NoteResponseTo> getAll() {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.GET_ALL)).responseTOs();
        }catch (TimeoutException e) {
            return webClient.get()
                    .uri("/api/v1.0/notes")
                    .retrieve()
                    .bodyToFlux(NoteResponseTo.class).collectList().block();
        }
    }
    @Cacheable(value = "notes", key = "#id")
    public NoteResponseTo getById(Long id) {
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.GET_BY_ID, id)).responseTOs().get(0);
        }catch (TimeoutException e) {
            return webClient.get()
                    .uri("/api/v1.0/notes/{id}", id)
                    .retrieve()
                    .bodyToMono(NoteResponseTo.class).block();
        }
    }
    @CachePut(value = "notes", key = "#req.id")
    public NoteResponseTo create(NoteRequestTo req) {
        long tweetId = req.getTweetId();
        if(tweetRepository.get(tweetId).isEmpty())
            throw new DataIntegrityViolationException("Tweet not found for id " + tweetId);
        UUID uuid = UUID.randomUUID();
        long id =  Math.abs(uuid.getMostSignificantBits());
        req.setId(id);
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.CREATE, req)).responseTOs().get(0);
        }catch (TimeoutException e) {
            return webClient.post()
                    .uri("/api/v1.0/notes")
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(NoteResponseTo.class).block();
        }
    }
    @CachePut(value = "notes", key = "#req.id")
    public NoteResponseTo update(NoteRequestTo req) {
        long tweetId = req.getTweetId();
        if(tweetRepository.get(tweetId).isEmpty())
            throw new DataIntegrityViolationException("Tweet not found for id " + tweetId);
        try {
            return kafkaClient.send(new MessageData(MessageData.Operation.UPDATE, req)).responseTOs().get(0);
        }catch (TimeoutException e) {
        return webClient.put()
                .uri("/api/v1.0/notes")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(NoteResponseTo.class).block();
        }
    }
    @CacheEvict(value = "notes", key = "#id")
    public void delete(Long id) {
        try {
            kafkaClient.send(new MessageData(MessageData.Operation.DELETE_BY_ID, id));
        }catch (TimeoutException e) {
            webClient.delete()
                    .uri("/api/v1.0/notes/{id}", id)
                    .retrieve()
                    .bodyToMono(Void.class).block();
        }
    }
}

