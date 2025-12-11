package com.task.rest.service;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.kafka.topic.KafkaCommentProducer;
import com.task.rest.kafka.topic.KafkaCommentResponser;
import com.task.rest.kafka.dto.KafkaCommentMessage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DiscussionClient {

    private final KafkaCommentProducer kafkaProducer;
    private final KafkaCommentResponser kafkaResponseHandler;
    private final WebClient webClient;

    public DiscussionClient(
            WebClient discussionWebClient,
            KafkaCommentProducer kafkaProducer,
            KafkaCommentResponser kafkaResponseHandler) {
        this.webClient = discussionWebClient;
        this.kafkaProducer = kafkaProducer;
        this.kafkaResponseHandler = kafkaResponseHandler;
    }

    // ======================
    // ЗАПИСЬ — через Kafka
    // ======================

    // ❌ НЕЛЬЗЯ использовать @Cacheable на create — нет id до вызова
    // Вместо этого используем @CachePut после создания
    @CachePut(value = "comments", key = "#result.id")
    public CommentResponseTo createComment(CommentRequestTo dto) {
        Long commentId = System.currentTimeMillis();
        KafkaCommentMessage message = new KafkaCommentMessage(
                commentId,
                dto.getTweetId(),
                "ru",
                dto.getContent(),
                "CREATE"
        );
        return sendAndAwaitResponse(message);
    }

    @CachePut(value = "comments", key = "#id")
    public CommentResponseTo updateComment(Long id, CommentRequestTo dto) {
        KafkaCommentMessage message = new KafkaCommentMessage();
        message.setId(id);
        message.setTweetId(dto.getTweetId());
        message.setCountry("ru");
        message.setContent(dto.getContent());
        message.setOperation("UPDATE");
        return sendAndAwaitResponse(message);
    }

    @CacheEvict(value = "comments", key = "#id")
    public void deleteComment(Long id) {
        KafkaCommentMessage message = new KafkaCommentMessage();
        message.setId(id);
        message.setOperation("DELETE");
        kafkaProducer.sendToInTopic(message);
    }

    // ======================
    // ЧТЕНИЕ — через REST
    // ======================

    @Cacheable(value = "comments", key = "#id")
    public CommentResponseTo getComment(Long id) {
        int x = 5;
        return webClient.get()
                .uri("/comments/{id}", id)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    // Для списков — создаём отдельные кеш-регионы или используем составные ключи
    @Cacheable(value = "commentsByTweet", key = "{#country, #tweetId}")
    public List<CommentResponseTo> getCommentsByCountryAndTweet(String country, Long tweetId) {
        return webClient.get()
                .uri("/comments/{country}/{tweetId}", country, tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    @Cacheable(value = "commentsByTweetId", key = "#tweetId")
    public List<CommentResponseTo> getCommentsByTweetId(Long tweetId) {
        return webClient.get()
                .uri("/comments/by-tweet/{tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    // getAllComments — не кэшируем, или используем отдельный ключ
    public List<CommentResponseTo> getAllComments() {
        return webClient.get()
                .uri("/comments")
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    // ======================
    // Вспомогательный метод
    // ======================
    private CommentResponseTo sendAndAwaitResponse(KafkaCommentMessage message) {
        if (message.getId() == null) {
            message.setId(System.currentTimeMillis());
        }
        if (message.getCreated() == null) {
            message.setCreated(java.time.LocalDateTime.now());
        }

        CompletableFuture<KafkaCommentMessage> future = kafkaResponseHandler.registerPendingResponse(message.getId());
        kafkaProducer.sendToInTopic(message);

        try {
            KafkaCommentMessage response = future.get(1, TimeUnit.SECONDS);
            return new CommentResponseTo(
                    response.getId(),
                    response.getTweetId(),
                    response.getCountry(),
                    response.getContent(),
                    response.getCreated(),
                    response.getCreated()
            );
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout waiting for response from discussion module", e);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to communicate with discussion module via Kafka", e);
        }
    }
}