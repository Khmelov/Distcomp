package com.distcomp.kafka;

import com.distcomp.common.kafka.NoticeKafkaRequest;
import com.distcomp.common.kafka.NoticeKafkaResponse;
import com.distcomp.exception.KafkaTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Synchronous request/reply facade over the asynchronous Kafka topics.
 *
 * <p>The publisher stores a future per {@code requestId}, sends the request to
 * {@code InTopic} and completes the future from the {@code OutTopic} listener.
 * The HTTP thread waits for the reply, so the external REST contract stays
 * synchronous and unchanged.</p>
 */
@Component
public class NoticeKafkaGateway {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String inTopic;

    private final long replyTimeoutMs;

    private final ConcurrentMap<String, CompletableFuture<NoticeKafkaResponse>> pending =
            new ConcurrentHashMap<>();

    public NoticeKafkaGateway(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${app.kafka.in-topic}") String inTopic,
                              @Value("${app.kafka.reply-timeout-ms:5000}") long replyTimeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.inTopic = inTopic;
        this.replyTimeoutMs = replyTimeoutMs;
    }

    public NoticeKafkaResponse request(NoticeKafkaRequest request) {
        CompletableFuture<NoticeKafkaResponse> future = new CompletableFuture<>();
        pending.put(request.requestId(), future);
        try {
            kafkaTemplate.send(inTopic, request.partitionKey(), request)
                    .get(replyTimeoutMs, TimeUnit.MILLISECONDS);
            NoticeKafkaResponse response = future.get(replyTimeoutMs, TimeUnit.MILLISECONDS);
            if (response == null) {
                throw new KafkaTransportException("Discussion module returned no reply");
            }
            return response;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new KafkaTransportException("Interrupted while waiting for a Kafka reply", exception);
        } catch (TimeoutException | ExecutionException exception) {
            throw new KafkaTransportException("Kafka request/reply failed or timed out", exception);
        } finally {
            pending.remove(request.requestId());
        }
    }

    @KafkaListener(topics = "${app.kafka.out-topic}",
            groupId = "${app.kafka.publisher-group}",
            autoStartup = "${app.kafka.enabled:true}")
    public void onReply(NoticeKafkaResponse response) {
        CompletableFuture<NoticeKafkaResponse> future = pending.remove(response.requestId());
        if (future != null) {
            future.complete(response);
        }
    }
}
