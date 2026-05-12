package org.rv.lab1.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rv.lab1.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Sends commands to {@code InTopic} with partition key = storyId; waits for matching {@code OutTopic} reply.
 */
@Component
public class CommentKafkaGateway {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String inTopic;
    private final ConcurrentHashMap<UUID, CompletableFuture<CommentReplyMessage>> pending = new ConcurrentHashMap<>();

    public CommentKafkaGateway(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.in-topic}") String inTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.inTopic = inTopic;
    }

    public CommentReplyMessage sendAndWait(CommentCommandMessage cmd) {
        if (cmd.storyId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 1, "storyId required for Kafka routing");
        }
        CompletableFuture<CommentReplyMessage> done = new CompletableFuture<>();
        pending.put(cmd.requestId(), done);
        try {
            String json = objectMapper.writeValueAsString(cmd);
            kafkaTemplate.send(inTopic, String.valueOf(cmd.storyId()), json).get(10, TimeUnit.SECONDS);
            return done.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, 2, "Discussion did not respond in time");
        } catch (ExecutionException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Kafka send failed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Interrupted");
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Serialize error");
        } finally {
            pending.remove(cmd.requestId(), done);
        }
    }

    public void complete(CommentReplyMessage reply) {
        if (reply == null || reply.requestId() == null) {
            return;
        }
        CompletableFuture<CommentReplyMessage> f = pending.remove(reply.requestId());
        if (f != null) {
            f.complete(reply);
        }
    }
}
