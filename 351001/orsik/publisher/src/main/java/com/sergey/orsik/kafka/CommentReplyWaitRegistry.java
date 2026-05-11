package com.sergey.orsik.kafka;

import com.sergey.orsik.dto.kafka.CommentTransportReply;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommentReplyWaitRegistry {

    private final ConcurrentHashMap<String, CompletableFuture<CommentTransportReply>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<CommentTransportReply> register(String correlationId) {
        CompletableFuture<CommentTransportReply> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(CommentTransportReply reply) {
        if (reply == null || reply.getCorrelationId() == null) {
            return;
        }
        CompletableFuture<CommentTransportReply> future = pending.remove(reply.getCorrelationId());
        if (future != null) {
            future.complete(reply);
        }
    }

    public void discard(String correlationId) {
        CompletableFuture<CommentTransportReply> future = pending.remove(correlationId);
        if (future != null) {
            future.cancel(true);
        }
    }
}
