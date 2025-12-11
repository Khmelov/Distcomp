package com.task.rest.service;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.kafka.topic.KafkaCommentProducer;
import com.task.rest.kafka.topic.KafkaCommentResponser;
import com.task.rest.kafka.dto.KafkaCommentMessage;
import com.task.rest.kafka.topic.KafkaCommentResponser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class DiscussionClient {

    // === Для записи: Kafka ===
    private final KafkaCommentProducer kafkaProducer;
    private final KafkaCommentResponser kafkaResponseHandler;

    // === Для чтения: REST (остаётся) ===
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

    public CommentResponseTo createComment(CommentRequestTo dto) {
        Long commentId = System.currentTimeMillis();
        KafkaCommentMessage message = new KafkaCommentMessage(
                commentId,
                dto.getTweetId(),
                "ru", // или передавать из DTO, если нужно
                dto.getContent(),
                "CREATE"
        );

        return sendAndAwaitResponse(message);
    }

    public CommentResponseTo updateComment(Long id, CommentRequestTo dto) {
        KafkaCommentMessage message = new KafkaCommentMessage();
        message.setId(id);
        message.setTweetId(dto.getTweetId());
        message.setCountry("ru");
        message.setContent(dto.getContent());
        message.setOperation("UPDATE");

        return sendAndAwaitResponse(message);
    }

    public void deleteComment(Long id) {
        KafkaCommentMessage message = new KafkaCommentMessage();
        message.setId(id);
        message.setOperation("DELETE");

        // Для удаления не ждём ответ (или можешь ждать — по желанию)
        kafkaProducer.sendToInTopic(message);
        // Можно добавить ожидание подтверждения, если нужно
    }

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
                    response.getCreated() // или отдельное modified, если будет
            );
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout waiting for response from discussion module", e);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to communicate with discussion module via Kafka", e);
        }
    }

    // ======================
    // ЧТЕНИЕ — через REST
    // ======================

    public CommentResponseTo getComment(Long id) {
        return webClient.get()
                .uri("/comments/{id}", id)
                .retrieve()
                .bodyToMono(CommentResponseTo.class)
                .block();
    }

    public List<CommentResponseTo> getAllComments() {
        return webClient.get()
                .uri("/comments")
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public List<CommentResponseTo> getCommentsByCountryAndTweet(String country, Long tweetId) {
        return webClient.get()
                .uri("/comments/{country}/{tweetId}", country, tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }

    public List<CommentResponseTo> getCommentsByTweetId(Long tweetId) {
        return webClient.get()
                .uri("/comments/by-tweet/{tweetId}", tweetId)
                .retrieve()
                .bodyToFlux(CommentResponseTo.class)
                .collectList()
                .block();
    }
}