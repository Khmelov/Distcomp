package com.socialnetwork.service;

import com.socialnetwork.dto.kafka.KafkaMessageResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaMessageService {

    private final ConcurrentHashMap<UUID, CompletableFuture<KafkaMessageResponse>> pendingRequests =
            new ConcurrentHashMap<>();

    public CompletableFuture<KafkaMessageResponse> waitForResponse(UUID requestId) {
        CompletableFuture<KafkaMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        // Таймаут 1 секунда как в требованиях
        future.completeOnTimeout(
                createTimeoutResponse(requestId),
                1,
                TimeUnit.SECONDS
        );

        return future;
    }

    public void completeRequest(UUID requestId, KafkaMessageResponse response) {
        CompletableFuture<KafkaMessageResponse> future = pendingRequests.remove(requestId);
        if (future != null) {
            future.complete(response);
        }
    }

    private KafkaMessageResponse createTimeoutResponse(UUID requestId) {
        KafkaMessageResponse response = new KafkaMessageResponse();
        response.setRequestId(requestId);
        response.setSuccess(false);
        response.setError("Request timeout");
        return response;
    }

    public String getPartitionKey(Long tweetId) {
        // Гарантируем, что сообщения одного твита попадают в одну партицию
        return "tweet-" + (tweetId != null ? tweetId : "all");
    }
}