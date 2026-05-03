package by.bsuir.distcomp.reaction;

import by.bsuir.distcomp.kafka.ReactionOutMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReactionReplyRegistry {

    private final ConcurrentHashMap<String, CompletableFuture<ReactionOutMessage>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<ReactionOutMessage> prepare(String correlationId) {
        CompletableFuture<ReactionOutMessage> f = new CompletableFuture<>();
        pending.put(correlationId, f);
        return f;
    }

    public void complete(String correlationId, ReactionOutMessage message) {
        CompletableFuture<ReactionOutMessage> f = pending.remove(correlationId);
        if (f != null) {
            f.complete(message);
        }
    }

    public void cancel(String correlationId) {
        CompletableFuture<ReactionOutMessage> f = pending.remove(correlationId);
        if (f != null) {
            f.cancel(true);
        }
    }
}
