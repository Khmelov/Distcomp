package org.polozkov.service.kafka;

import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.other.record.CommentResponseRecord;
import org.polozkov.other.record.CommentUploadRecord;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, CommentUploadRecord> kafkaTemplate;

    private final Map<UUID, CompletableFuture<List<CommentResponseTo>>> futures = new ConcurrentHashMap<>();

    public void sendRequest(CommentUploadRecord record) {
        kafkaTemplate.send("in-topic", record.id().toString(), record);
    }

    public List<CommentResponseTo> sendAndReceive(CommentUploadRecord record) {
        CompletableFuture<List<CommentResponseTo>> future = new CompletableFuture<>();
        futures.put(record.id(), future);

        sendRequest(record);

        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            futures.remove(record.id());
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Discussion service timeout");
        } catch (Exception e) {
            futures.remove(record.id());
            throw new RuntimeException("Error waiting for Kafka response", e);
        }
    }

    @KafkaListener(topics = "out-topic", groupId = "publisher-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(CommentResponseRecord response) {
        CompletableFuture<List<CommentResponseTo>> future = futures.remove(response.id());
        if (future != null) {
            if (response.error() != null) {
                future.completeExceptionally(new RuntimeException(response.error()));
            } else {
                future.complete(response.data());
            }
        }
    }
}
