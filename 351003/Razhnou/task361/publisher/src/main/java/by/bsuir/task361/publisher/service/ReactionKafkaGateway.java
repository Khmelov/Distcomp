package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.config.CacheNames;
import by.bsuir.task361.publisher.dto.ReactionState;
import by.bsuir.task361.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.exception.ApiException;
import by.bsuir.task361.publisher.exception.BadRequestException;
import by.bsuir.task361.publisher.kafka.dto.ReactionKafkaRequest;
import by.bsuir.task361.publisher.kafka.dto.ReactionKafkaResponse;
import by.bsuir.task361.publisher.kafka.dto.ReactionOperation;
import by.bsuir.task361.publisher.kafka.dto.ReactionPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReactionKafkaGateway {
    private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis());
    private final Map<String, CompletableFuture<ReactionKafkaResponse>> pendingResponses = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PublisherCacheService cacheService;
    private final String inTopic;
    private final long timeoutMs;

    public ReactionKafkaGateway(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            PublisherCacheService cacheService,
            @Value("${app.kafka.reaction.in-topic}") String inTopic,
            @Value("${app.kafka.reaction.timeout-ms}") long timeoutMs
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.cacheService = cacheService;
        this.inTopic = inTopic;
        this.timeoutMs = timeoutMs;
    }

    public ReactionResponseTo create(ReactionRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Reaction id must be null on create", 3);
        }
        validateTweetId(request.tweetId());
        validateContent(request.content());

        Long reactionId = sequence.incrementAndGet();
        ReactionPayload payload = new ReactionPayload(reactionId, request.tweetId(), request.content().trim(), ReactionState.PENDING);
        ReactionKafkaResponse response = sendAndAwait(new ReactionKafkaRequest(
                newRequestId(),
                ReactionOperation.CREATE,
                request.tweetId(),
                reactionId,
                payload
        ), String.valueOf(request.tweetId()));
        ReactionResponseTo reaction = requirePayload(response);
        cacheService.put(CacheNames.REACTIONS, reaction.id(), reaction);
        return reaction;
    }

    public List<ReactionResponseTo> findAll() {
        ReactionKafkaResponse response = sendAndAwait(new ReactionKafkaRequest(
                newRequestId(),
                ReactionOperation.READ_ALL,
                null,
                null,
                null
        ), "all");
        ensureSuccess(response);
        return response.payloadList() == null
                ? List.of()
                : response.payloadList().stream().map(this::toResponse).toList();
    }

    public ReactionResponseTo findById(Long id) {
        validateId(id, "Reaction id");
        ReactionResponseTo cached = cacheService.get(CacheNames.REACTIONS, id, ReactionResponseTo.class);
        if (cached != null) {
            return cached;
        }
        ReactionKafkaResponse response = sendAndAwait(new ReactionKafkaRequest(
                newRequestId(),
                ReactionOperation.READ_ONE,
                null,
                id,
                null
        ), String.valueOf(id));
        ReactionResponseTo reaction = requirePayload(response);
        cacheService.put(CacheNames.REACTIONS, reaction.id(), reaction);
        return reaction;
    }

    public ReactionResponseTo update(ReactionRequestTo request) {
        validateId(request.id(), "Reaction id");
        validateTweetId(request.tweetId());
        validateContent(request.content());

        ReactionPayload payload = new ReactionPayload(
                request.id(),
                request.tweetId(),
                request.content().trim(),
                request.state() == null ? ReactionState.PENDING : request.state()
        );
        ReactionKafkaResponse response = sendAndAwait(new ReactionKafkaRequest(
                newRequestId(),
                ReactionOperation.UPDATE,
                request.tweetId(),
                request.id(),
                payload
        ), String.valueOf(request.tweetId()));
        ReactionResponseTo reaction = requirePayload(response);
        cacheService.put(CacheNames.REACTIONS, reaction.id(), reaction);
        return reaction;
    }

    public void delete(Long id) {
        validateId(id, "Reaction id");
        ReactionKafkaResponse response = sendAndAwait(new ReactionKafkaRequest(
                newRequestId(),
                ReactionOperation.DELETE,
                null,
                id,
                null
        ), String.valueOf(id));
        ensureSuccess(response);
        cacheService.evict(CacheNames.REACTIONS, id);
    }

    public void complete(ReactionKafkaResponse response) {
        CompletableFuture<ReactionKafkaResponse> future = pendingResponses.remove(response.requestId());
        if (future != null) {
            future.complete(response);
        }
    }

    private ReactionKafkaResponse sendAndAwait(ReactionKafkaRequest request, String key) {
        CompletableFuture<ReactionKafkaResponse> future = new CompletableFuture<>();
        pendingResponses.put(request.requestId(), future);
        try {
            kafkaTemplate.send(inTopic, key, objectMapper.writeValueAsString(request));
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "Failed to serialize Kafka request");
        } catch (TimeoutException exception) {
            throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, 50401, "Reaction service response timeout");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "Reaction request was interrupted");
        } catch (ExecutionException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50004, "Reaction service request failed");
        } finally {
            pendingResponses.remove(request.requestId());
        }
    }

    private ReactionResponseTo requirePayload(ReactionKafkaResponse response) {
        ensureSuccess(response);
        if (response.payload() == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50005, "Reaction response payload is missing");
        }
        return toResponse(response.payload());
    }

    private void ensureSuccess(ReactionKafkaResponse response) {
        if (response.success()) {
            return;
        }
        HttpStatus status = HttpStatus.resolve(response.status());
        HttpStatus resolvedStatus = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        int errorCode = response.errorCode() == null ? resolvedStatus.value() * 100 + 1 : response.errorCode();
        String message = response.errorMessage() == null || response.errorMessage().isBlank()
                ? "Reaction service request failed"
                : response.errorMessage();
        throw new ApiException(resolvedStatus, errorCode, message);
    }

    private ReactionResponseTo toResponse(ReactionPayload payload) {
        return new ReactionResponseTo(payload.id(), payload.tweetId(), payload.content(), payload.state());
    }

    private String newRequestId() {
        return UUID.randomUUID().toString();
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateTweetId(Long tweetId) {
        if (tweetId == null || tweetId <= 0) {
            throw new BadRequestException("Tweet id must be greater than 0", 2);
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException("Reaction content must not be blank", 4);
        }
        int length = content.trim().length();
        if (length < 2 || length > 2048) {
            throw new BadRequestException("Reaction content length must be between 2 and 2048", 5);
        }
    }
}
