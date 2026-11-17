package by.bsuir.rest.kafka;

import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class MessageReplyWaiter {

    static final long TIMEOUT_SECONDS = 1;

    private final ConcurrentMap<String, CompletableFuture<MessageEvent>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<MessageEvent> register(String correlationId) {
        CompletableFuture<MessageEvent> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(MessageEvent event) {
        if (event == null || event.getCorrelationId() == null) {
            return;
        }
        CompletableFuture<MessageEvent> future = pending.remove(event.getCorrelationId());
        if (future != null) {
            future.complete(event);
        }
    }

    public MessageEvent await(CompletableFuture<MessageEvent> future, String correlationId) {
        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException exception) {
            pending.remove(correlationId);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL,
                    "Discussion did not respond within 1 second");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            pending.remove(correlationId);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL,
                    "Waiting for discussion was interrupted");
        } catch (ExecutionException exception) {
            pending.remove(correlationId);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL,
                    "Discussion response failed");
        }
    }
}
