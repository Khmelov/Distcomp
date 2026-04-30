package com.apigateway.messages;

import com.apigateway.messages.dto.MessageRequestTo;
import com.apigateway.messages.dto.MessageResponseTo;
import com.apigateway.messages.kafka.MessageCommandEvent;
import com.apigateway.messages.kafka.MessageOperation;
import com.apigateway.messages.kafka.MessageResultEvent;
import com.apigateway.messages.kafka.MessageState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@RestController
public class MessagePublisherController {

    private final MessageKafkaGateway messageKafkaGateway;
    private final MessageRestClient messageRestClient;

    public MessagePublisherController(MessageKafkaGateway messageKafkaGateway, MessageRestClient messageRestClient) {
        this.messageKafkaGateway = messageKafkaGateway;
        this.messageRestClient = messageRestClient;
    }

    @GetMapping({
            "/api/v1.0/messages",
            "/api/v1.0/messages/**",
            "/api/v2.0/messages",
            "/api/v2.0/messages/**"
    })
    public Mono<ResponseEntity<String>> proxyGetMessages(ServerWebExchange exchange) {
        return messageRestClient.proxyGet(exchange);
    }

    @PostMapping({"/api/v1.0/messages", "/api/v2.0/messages"})
    public Mono<ResponseEntity<?>> createMessage(@RequestBody MessageRequestTo request, ServerWebExchange exchange) {
        ResponseEntity<?> validationError = validateRequest(request, false);
        if (validationError != null) {
            return Mono.just(validationError);
        }

        if (isV1(exchange)) {
            return rest(() -> messageRestClient.create(request));
        }

        MessageCommandEvent command = command(MessageOperation.CREATE, null, request.getTweetId(), request.getContent());
        return dispatch(command, HttpStatus.CREATED, () -> messageRestClient.create(request));
    }

    @PutMapping({"/api/v1.0/messages/{id}", "/api/v2.0/messages/{id}"})
    public Mono<ResponseEntity<?>> updateMessage(@PathVariable Long id, @RequestBody MessageRequestTo request, ServerWebExchange exchange) {
        ResponseEntity<?> validationError = validateRequest(request, false);
        if (validationError != null) {
            return Mono.just(validationError);
        }

        if (isV1(exchange)) {
            return rest(() -> messageRestClient.update(id, request));
        }

        MessageCommandEvent command = command(MessageOperation.UPDATE, id, request.getTweetId(), request.getContent());
        return dispatch(command, HttpStatus.OK, () -> messageRestClient.update(id, request));
    }

    @PutMapping({"/api/v1.0/messages", "/api/v2.0/messages"})
    public Mono<ResponseEntity<?>> updateMessage(@RequestBody MessageRequestTo request, ServerWebExchange exchange) {
        ResponseEntity<?> validationError = validateRequest(request, true);
        if (validationError != null) {
            return Mono.just(validationError);
        }

        if (isV1(exchange)) {
            return rest(() -> messageRestClient.update(request.getId(), request));
        }

        MessageCommandEvent command = command(MessageOperation.UPDATE, request.getId(), request.getTweetId(), request.getContent());
        return dispatch(command, HttpStatus.OK, () -> messageRestClient.update(request.getId(), request));
    }

    @DeleteMapping({"/api/v1.0/messages/{id}", "/api/v2.0/messages/{id}"})
    public Mono<ResponseEntity<?>> deleteMessage(@PathVariable Long id, ServerWebExchange exchange) {
        if (isV1(exchange)) {
            return rest(() -> messageRestClient.deleteById(id));
        }

        MessageCommandEvent command = command(MessageOperation.DELETE_BY_ID, id, null, null);
        return dispatch(command, HttpStatus.NO_CONTENT, () -> messageRestClient.deleteById(id));
    }

    @DeleteMapping({"/api/v1.0/messages/tweets/{tweetId}", "/api/v2.0/messages/tweets/{tweetId}"})
    public Mono<ResponseEntity<?>> deleteMessagesByTweet(@PathVariable Long tweetId, ServerWebExchange exchange) {
        if (isV1(exchange)) {
            return rest(() -> messageRestClient.deleteByTweetId(tweetId));
        }

        MessageCommandEvent command = command(MessageOperation.DELETE_BY_TWEET, null, tweetId, null);
        return dispatch(command, HttpStatus.NO_CONTENT, () -> messageRestClient.deleteByTweetId(tweetId));
    }

    private Mono<ResponseEntity<?>> dispatch(
            MessageCommandEvent command,
            HttpStatus successStatus,
            Supplier<Mono<ResponseEntity<String>>> fallback) {
        return Mono.fromFuture(messageKafkaGateway.send(command))
                .<ResponseEntity<?>>map(result -> toResponse(result, successStatus))
                .onErrorResume(TimeoutException.class, error -> rest(fallback))
                .onErrorResume(error -> rest(fallback));
    }

    private Mono<ResponseEntity<?>> rest(Supplier<Mono<ResponseEntity<String>>> request) {
        return request.get().map(response -> (ResponseEntity<?>) response);
    }

    private boolean isV1(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().pathWithinApplication().value().startsWith("/api/v1.0/");
    }

    private ResponseEntity<?> toResponse(MessageResultEvent result, HttpStatus successStatus) {
        if (!result.isSuccess()) {
            return errorResponse(statusByErrorCode(result.getErrorCode()), result.getErrorMessage());
        }

        if (successStatus == HttpStatus.NO_CONTENT) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(successStatus).body(MessageResponseTo.from(result));
    }

    private HttpStatus statusByErrorCode(String errorCode) {
        return switch (errorCode == null ? "" : errorCode) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "BAD_REQUEST", "VALIDATION" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ResponseEntity<?> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message == null ? status.getReasonPhrase() : message));
    }

    private ResponseEntity<?> validateRequest(MessageRequestTo request, boolean requireId) {
        if (request == null) {
            return errorResponse(HttpStatus.BAD_REQUEST, "Message request is required");
        }
        if (requireId && request.getId() == null) {
            return errorResponse(HttpStatus.BAD_REQUEST, "Message id is required");
        }
        if (request.getTweetId() == null) {
            return errorResponse(HttpStatus.BAD_REQUEST, "Tweet id is required");
        }
        if (request.getContent() == null || request.getContent().length() < 2 || request.getContent().length() > 2048) {
            return errorResponse(HttpStatus.BAD_REQUEST, "Message content length must be between 2 and 2048");
        }
        return null;
    }

    private MessageCommandEvent command(MessageOperation operation, Long messageId, Long tweetId, String content) {
        MessageCommandEvent command = new MessageCommandEvent();
        command.setCorrelationId(UUID.randomUUID().toString());
        command.setOperation(operation);
        command.setMessageId(messageId);
        command.setTweetId(tweetId);
        command.setContent(content);
        command.setState(MessageState.PENDING);
        return command;
    }
}
