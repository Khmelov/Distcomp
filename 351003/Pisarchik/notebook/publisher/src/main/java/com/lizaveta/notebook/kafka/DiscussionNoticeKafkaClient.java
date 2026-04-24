package com.lizaveta.notebook.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lizaveta.notebook.client.DiscussionNoticeClient;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.model.NoticeState;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.config.KafkaJacksonConfig;
import com.lizaveta.notebook.service.NoticeIdGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@ConditionalOnProperty(name = "discussion.transport", havingValue = "kafka")
public class DiscussionNoticeKafkaClient implements DiscussionNoticeClient {

    private static final int TIMEOUT_CODE = 50002;
    private static final String LIST_PARTITION_KEY = "list";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper kafkaObjectMapper;
    private final PendingKafkaReplyRegistry pendingReplies;
    private final NoticeIdGenerator idGenerator;
    private final String inTopic;
    private final long replyTimeoutMs;

    public DiscussionNoticeKafkaClient(
            final KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier(KafkaJacksonConfig.KAFKA_OBJECT_MAPPER) final ObjectMapper kafkaObjectMapper,
            final PendingKafkaReplyRegistry pendingReplies,
            final NoticeIdGenerator idGenerator,
            @Value("${kafka.topic.in-topic}") final String inTopic,
            @Value("${kafka.sync.reply-timeout-ms:1000}") final long replyTimeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaObjectMapper = kafkaObjectMapper;
        this.pendingReplies = pendingReplies;
        this.idGenerator = idGenerator;
        this.inTopic = inTopic;
        this.replyTimeoutMs = replyTimeoutMs;
    }

    @Override
    public NoticeResponseTo create(final NoticeRequestTo request) {
        long id = idGenerator.nextId();
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("id", id);
        payload.put("storyId", request.storyId());
        payload.put("content", request.content());
        NoticeKafkaInEnvelope envelope = new NoticeKafkaInEnvelope(NoticeInMessageType.DRAFT_CREATE, null, payload);
        try {
            kafkaTemplate.send(inTopic, String.valueOf(request.storyId()), kafkaObjectMapper.writeValueAsString(envelope))
                    .get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ValidationException("Interrupted", 50001);
        } catch (ExecutionException ex) {
            throw new ValidationException("Failed to send notice: " + ex.getMessage(), 50001);
        } catch (JsonProcessingException ex) {
            throw new ValidationException("Failed to send notice: " + ex.getMessage(), 50001);
        }
        return new NoticeResponseTo(id, request.storyId(), request.content(), NoticeState.PENDING);
    }

    @Override
    public List<NoticeResponseTo> findAllAsList() {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        NoticeKafkaOutEnvelope out = awaitSyncReply(NoticeInMessageType.GET_ALL_LIST, payload, LIST_PARTITION_KEY);
        assertSuccess(out);
        return kafkaObjectMapper.convertValue(out.payload(), new TypeReference<List<NoticeResponseTo>>() {
        });
    }

    @Override
    public PageResponseTo<NoticeResponseTo> findAllPaged(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("page", page);
        payload.put("size", size);
        if (sortBy != null) {
            payload.put("sortBy", sortBy);
        }
        payload.put("sortOrder", sortOrder == null ? "asc" : sortOrder);
        NoticeKafkaOutEnvelope out = awaitSyncReply(NoticeInMessageType.GET_ALL_PAGE, payload, LIST_PARTITION_KEY);
        assertSuccess(out);
        return kafkaObjectMapper.convertValue(out.payload(), new TypeReference<PageResponseTo<NoticeResponseTo>>() {
        });
    }

    @Override
    public NoticeResponseTo findById(final Long id) {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("id", id);
        NoticeKafkaOutEnvelope out = awaitSyncReply(NoticeInMessageType.GET_BY_ID, payload, String.valueOf(id));
        assertSuccess(out);
        return kafkaObjectMapper.convertValue(out.payload(), NoticeResponseTo.class);
    }

    @Override
    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("storyId", storyId);
        NoticeKafkaOutEnvelope out =
                awaitSyncReply(NoticeInMessageType.GET_BY_STORY, payload, String.valueOf(storyId));
        assertSuccess(out);
        return kafkaObjectMapper.convertValue(out.payload(), new TypeReference<List<NoticeResponseTo>>() {
        });
    }

    @Override
    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("id", id);
        payload.put("storyId", request.storyId());
        payload.put("content", request.content());
        NoticeKafkaOutEnvelope out =
                awaitSyncReply(NoticeInMessageType.UPDATE, payload, String.valueOf(request.storyId()));
        assertSuccess(out);
        return kafkaObjectMapper.convertValue(out.payload(), NoticeResponseTo.class);
    }

    @Override
    public void deleteById(final Long id) {
        ObjectNode payload = kafkaObjectMapper.createObjectNode();
        payload.put("id", id);
        NoticeKafkaOutEnvelope out = awaitSyncReply(NoticeInMessageType.DELETE, payload, String.valueOf(id));
        assertSuccess(out);
    }

    private NoticeKafkaOutEnvelope awaitSyncReply(
            final NoticeInMessageType type,
            final JsonNode payload,
            final String partitionKey) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<NoticeKafkaOutEnvelope> future = new CompletableFuture<>();
        pendingReplies.register(correlationId, future);
        try {
            NoticeKafkaInEnvelope envelope = new NoticeKafkaInEnvelope(type, correlationId, payload);
            kafkaTemplate.send(inTopic, partitionKey, kafkaObjectMapper.writeValueAsString(envelope)).get();
        } catch (InterruptedException ex) {
            pendingReplies.cancel(correlationId);
            Thread.currentThread().interrupt();
            throw new ValidationException("Interrupted", 50001);
        } catch (ExecutionException ex) {
            pendingReplies.cancel(correlationId);
            throw new ValidationException("Kafka send failed: " + ex.getMessage(), 50001);
        } catch (Exception ex) {
            pendingReplies.cancel(correlationId);
            throw new ValidationException("Kafka send failed: " + ex.getMessage(), 50001);
        }
        try {
            return future.get(replyTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            pendingReplies.cancel(correlationId);
            throw new ValidationException("Discussion service timeout", TIMEOUT_CODE);
        } catch (InterruptedException ex) {
            pendingReplies.cancel(correlationId);
            Thread.currentThread().interrupt();
            throw new ValidationException("Interrupted", 50001);
        } catch (ExecutionException ex) {
            pendingReplies.cancel(correlationId);
            throw new ValidationException("Discussion service error", 50001);
        }
    }

    private void assertSuccess(final NoticeKafkaOutEnvelope out) {
        if (!out.success()) {
            int code = out.errorCode() != null ? out.errorCode() : 50001;
            String message = out.errorMessage() != null ? out.errorMessage() : "Error";
            if (code == 40401) {
                throw new ResourceNotFoundException(message);
            }
            throw new ValidationException(message, code);
        }
    }
}
