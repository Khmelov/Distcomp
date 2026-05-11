package com.adashkevich.redis.lab.kafka;

import com.adashkevich.redis.lab.config.KafkaTopicProperties;
import com.adashkevich.redis.lab.exception.ConflictException;
import com.adashkevich.redis.lab.exception.ForbiddenException;
import com.adashkevich.redis.lab.exception.GatewayTimeoutException;
import com.adashkevich.redis.lab.exception.NotFoundException;
import com.adashkevich.redis.lab.exception.ValidationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaRequestReplyClient {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties properties;
    private final ConcurrentMap<String, CompletableFuture<KafkaReplyMessage>> pendingReplies = new ConcurrentHashMap<>();

    public KafkaRequestReplyClient(KafkaTemplate<String, Object> kafkaTemplate, KafkaTopicProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public KafkaReplyMessage sendAndReceive(KafkaRequestMessage request) {
        String correlationId = UUID.randomUUID().toString();
        request.setCorrelationId(correlationId);
        CompletableFuture<KafkaReplyMessage> future = new CompletableFuture<>();
        pendingReplies.put(correlationId, future);
        try {
            kafkaTemplate.send(properties.getInTopic(), String.valueOf(request.getNewsId()), request);
            KafkaReplyMessage reply = future.get(properties.getReplyTimeoutMs(), TimeUnit.MILLISECONDS);
            if (!reply.isSuccess()) {
                throw mapRemoteException(reply);
            }
            return reply;
        } catch (TimeoutException ex) {
            throw new GatewayTimeoutException("Kafka reply timeout", "50400");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new GatewayTimeoutException("Kafka request interrupted", "50401");
        } catch (java.util.concurrent.ExecutionException ex) {
            throw new GatewayTimeoutException("Kafka request failed", "50402");
        } finally {
            pendingReplies.remove(correlationId);
        }
    }

    @KafkaListener(topics = "${app.kafka.out-topic}", groupId = "publisher-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeReply(KafkaReplyMessage reply) {
        CompletableFuture<KafkaReplyMessage> future = pendingReplies.get(reply.getCorrelationId());
        if (future != null) {
            future.complete(reply);
        }
    }

    private RuntimeException mapRemoteException(KafkaReplyMessage reply) {
        return switch (reply.getHttpStatus()) {
            case 400 -> new ValidationException(reply.getErrorMessage(), reply.getErrorCode());
            case 403 -> new ForbiddenException(reply.getErrorMessage(), reply.getErrorCode());
            case 404 -> new NotFoundException(reply.getErrorMessage(), reply.getErrorCode());
            case 409 -> new ConflictException(reply.getErrorMessage(), reply.getErrorCode());
            default -> new GatewayTimeoutException(reply.getErrorMessage(), reply.getErrorCode() == null ? "50000" : reply.getErrorCode());
        };
    }
}
