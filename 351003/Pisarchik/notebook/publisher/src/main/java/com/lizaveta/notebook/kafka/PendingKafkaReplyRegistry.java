package com.lizaveta.notebook.kafka;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingKafkaReplyRegistry {

    private final ConcurrentHashMap<String, CompletableFuture<NoticeKafkaOutEnvelope>> pending =
            new ConcurrentHashMap<>();

    public void register(final String correlationId, final CompletableFuture<NoticeKafkaOutEnvelope> future) {
        pending.put(correlationId, future);
    }

    public void complete(final String correlationId, final NoticeKafkaOutEnvelope envelope) {
        CompletableFuture<NoticeKafkaOutEnvelope> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(envelope);
        }
    }

    public void cancel(final String correlationId) {
        CompletableFuture<NoticeKafkaOutEnvelope> future = pending.remove(correlationId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
