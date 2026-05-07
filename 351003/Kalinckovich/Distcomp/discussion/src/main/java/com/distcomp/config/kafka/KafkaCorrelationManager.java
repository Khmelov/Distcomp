package com.distcomp.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KafkaCorrelationManager {

    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
    private static final long DEFAULT_TIMEOUT_SECONDS = 30;

    public String registerRequest() {
        String correlationId = java.util.UUID.randomUUID().toString();
        pendingRequests.put(correlationId, new CompletableFuture<>());
        log.debug("Registered pending request: {}", correlationId);
        return correlationId;
    }

    public CompletableFuture<Object> getFuture(String correlationId) {
        return pendingRequests.get(correlationId);
    }

    public void completeRequest(String correlationId, Object response) {
        CompletableFuture<Object> future = pendingRequests.remove(correlationId);
        if (future != null) {
            log.debug("Completed request: {}", correlationId);
            future.complete(response);
        } else {
            log.warn("No pending request found for correlationId: {}", correlationId);
        }
    }

    public void completeRequestWithError(String correlationId, Throwable error) {
        CompletableFuture<Object> future = pendingRequests.remove(correlationId);
        if (future != null) {
            log.debug("Failed request: {}", correlationId, error);
            future.completeExceptionally(error);
        } else {
            log.warn("No pending request found for correlationId: {}", correlationId);
        }
    }

    public <T> T waitForResponse(String correlationId, Class<T> responseType, long timeout, TimeUnit unit)
            throws Exception {
        CompletableFuture<Object> future = pendingRequests.get(correlationId);
        if (future == null) {
            throw new IllegalStateException("No pending request for correlationId: " + correlationId);
        }

        try {
            Object result = future.get(timeout, unit);
            pendingRequests.remove(correlationId);

            if (result instanceof Exception) {
                throw (Exception) result;
            }

            return responseType.cast(result);

        } catch (ExecutionException e) {
            
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        } finally {
            pendingRequests.remove(correlationId);
        }
    }

    public void cleanupTimedOutRequests() {
        
    }
}