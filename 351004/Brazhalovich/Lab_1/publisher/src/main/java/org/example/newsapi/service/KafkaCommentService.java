package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.newsapi.dto.kafka.CommentKafkaMessage;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentService {

    private final KafkaTemplate<String, CommentKafkaMessage> kafkaTemplate;
    private final IdGenerator idGenerator;

    @Value("${app.kafka.in-topic}")
    private String inTopic;

    @Value("${app.kafka.out-topic}")
    private String outTopic;

    private final ConcurrentHashMap<Long, CompletableFuture<CommentKafkaMessage>> pendingRequests = new ConcurrentHashMap<>();

    public CommentResponseTo createComment(CommentRequestTo request) {
        Long id = idGenerator.nextId();
        CommentKafkaMessage message = new CommentKafkaMessage(id, request.getNewsId(), request.getContent(), "PENDING");

        CompletableFuture<CommentKafkaMessage> future = new CompletableFuture<>();
        pendingRequests.put(id, future);

        // Отправляем с ключом = newsId для гарантии порядка в партиции
        kafkaTemplate.send(inTopic, String.valueOf(request.getNewsId()), message);

        try {
            CommentKafkaMessage response = future.get(1, TimeUnit.SECONDS);
            if ("APPROVE".equals(response.getState())) {
                return new CommentResponseTo(response.getId(), response.getNewsId(), response.getContent());
            } else {
                throw new RuntimeException("Comment declined by moderator");
            }
        } catch (Exception e) {
            log.error("Error while waiting for Kafka response", e);
            throw new RuntimeException("Comment processing timeout or error");
        } finally {
            pendingRequests.remove(id);
        }
    }

    @KafkaListener(topics = "${app.kafka.out-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentKafkaMessage message) {
        log.info("Received response from OutTopic: {}", message);
        CompletableFuture<CommentKafkaMessage> future = pendingRequests.get(message.getId());
        if (future != null) {
            future.complete(message);
        } else {
            log.warn("No pending request for comment id: {}", message.getId());
        }
    }
}