package com.example.demo.labrest.service;

import com.example.demo.labrest.dto.KafkaNoticeRequest;
import com.example.demo.labrest.dto.KafkaNoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeKafkaBridge {

    private final KafkaTemplate<String, KafkaNoticeRequest> kafkaTemplate;

    private final Map<String, CompletableFuture<KafkaNoticeResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<KafkaNoticeResponse> sendRequest(KafkaNoticeRequest request) {
        String correlationId = UUID.randomUUID().toString();
        request.setCorrelationId(correlationId);

        CompletableFuture<KafkaNoticeResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        kafkaTemplate.send("InTopic", String.valueOf(request.getTopicId()), request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        pendingRequests.remove(correlationId);
                        future.completeExceptionally(ex);
                        log.error("Failed to send request to Kafka", ex);
                    } else {
                        log.debug("Request sent to InTopic: correlationId={}", correlationId);
                    }
                });

        return future;
    }

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void handleResponse(KafkaNoticeResponse response) {
        String correlationId = response.getCorrelationId();
        if (correlationId != null) {
            CompletableFuture<KafkaNoticeResponse> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(response);
                log.debug("Response received: correlationId={}, state={}", correlationId, response.getState());
            } else {
                log.debug("No pending request for correlationId: {}", correlationId);
            }
        }
    }
}