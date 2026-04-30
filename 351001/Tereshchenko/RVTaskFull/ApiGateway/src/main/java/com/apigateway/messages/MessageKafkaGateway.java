package com.apigateway.messages;

import com.apigateway.messages.kafka.MessageCommandEvent;
import com.apigateway.messages.kafka.MessageResultEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class MessageKafkaGateway {

    private final KafkaTemplate<String, MessageCommandEvent> kafkaTemplate;
    private final String inTopic;
    private final long requestTimeoutMs;
    private final Map<String, CompletableFuture<MessageResultEvent>> pending = new ConcurrentHashMap<>();

    public MessageKafkaGateway(
            KafkaTemplate<String, MessageCommandEvent> kafkaTemplate,
            @Value("${app.kafka.in-topic}") String inTopic,
            @Value("${app.kafka.request-timeout-ms:10000}") long requestTimeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.inTopic = inTopic;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public CompletableFuture<MessageResultEvent> send(MessageCommandEvent command) {
        CompletableFuture<MessageResultEvent> response = new CompletableFuture<>();
        pending.put(command.getCorrelationId(), response);

        kafkaTemplate.send(inTopic, partitionKey(command), command)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        pending.remove(command.getCorrelationId());
                        response.completeExceptionally(error);
                    }
                });

        return response.orTimeout(requestTimeoutMs, TimeUnit.MILLISECONDS)
                .whenComplete((result, error) -> pending.remove(command.getCorrelationId()));
    }

    @KafkaListener(
            topics = "${app.kafka.out-topic}",
            groupId = "${app.kafka.publisher-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeResult(MessageResultEvent event) {
        CompletableFuture<MessageResultEvent> response = pending.get(event.getCorrelationId());
        if (response != null) {
            response.complete(event);
        }
    }

    private String partitionKey(MessageCommandEvent command) {
        Long key = command.getTweetId() != null ? command.getTweetId() : command.getMessageId();
        return Objects.toString(key, command.getCorrelationId());
    }
}
