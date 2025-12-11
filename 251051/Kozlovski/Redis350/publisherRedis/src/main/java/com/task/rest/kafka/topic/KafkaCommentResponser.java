package com.task.rest.kafka.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.rest.kafka.dto.KafkaCommentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaCommentResponser {

    private static final Logger log = LoggerFactory.getLogger(KafkaCommentResponser.class);

    // Храним ожидающие ответы: id комментария -> CompletableFuture
    private final ConcurrentHashMap<Long, CompletableFuture<KafkaCommentMessage>> pendingResponses = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public KafkaCommentResponser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Регистрирует ожидание ответа и возвращает Future
     */
    public CompletableFuture<KafkaCommentMessage> registerPendingResponse(Long commentId) {
        CompletableFuture<KafkaCommentMessage> future = new CompletableFuture<>();
        pendingResponses.put(commentId, future);
        return future;
    }

    /**
     * Обработка ответа из OutTopic
     */
    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void handleResponse(Map<String, Object> message1) {
        var message = objectMapper.convertValue(message1, KafkaCommentMessage.class);
        log.info("Received from OutTopic: {}", message);
        CompletableFuture<KafkaCommentMessage> future = pendingResponses.remove(message.getId());
        if (future != null) {
            future.complete(message);
        } else {
            log.warn("No pending request for comment ID: {}", message.getId());
        }
    }
}