package by.bsuir.rest.client;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
@ConditionalOnProperty(name = "discussion.client", havingValue = "http")
public class RestDiscussionClient implements DiscussionClient {

    private static final Logger log = LoggerFactory.getLogger(RestDiscussionClient.class);
    private static final ParameterizedTypeReference<List<MessageResponseTo>> MESSAGE_LIST =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public RestDiscussionClient(RestClient discussionRestClient, ObjectMapper objectMapper) {
        this.restClient = discussionRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        return call(() -> restClient.post()
                .uri("/messages")
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class), null);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        return call(() -> restClient.get()
                .uri("/messages/{id}", id)
                .retrieve()
                .body(MessageResponseTo.class), id);
    }

    @Override
    public List<MessageResponseTo> getAll() {
        List<MessageResponseTo> body = call(() -> restClient.get()
                .uri("/messages")
                .retrieve()
                .body(MESSAGE_LIST), null);
        return body == null ? List.of() : body;
    }

    @Override
    public List<MessageResponseTo> findByStoryId(Long storyId) {
        List<MessageResponseTo> body = call(() -> restClient.get()
                .uri("/messages?storyId={storyId}", storyId)
                .retrieve()
                .body(MESSAGE_LIST), null);
        return body == null ? List.of() : body;
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        request.setId(id);
        return call(() -> restClient.put()
                .uri("/messages/{id}", id)
                .body(request)
                .retrieve()
                .body(MessageResponseTo.class), id);
    }

    @Override
    public void delete(Long id) {
        call(() -> {
            restClient.delete()
                    .uri("/messages/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
            return null;
        }, id);
    }

    private <T> T call(RemoteCall<T> remoteCall, Long id) {
        try {
            return remoteCall.execute();
        } catch (RestClientResponseException exception) {
            throw translate(exception, id);
        } catch (ResourceAccessException exception) {
            log.warn("Discussion is unavailable: {}", exception.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL,
                    "Discussion service is unavailable");
        }
    }

    private ApiException translate(RestClientResponseException exception, Long id) {
        int status = exception.getStatusCode().value();
        JsonNode body = readBody(exception);
        if (body != null && body.hasNonNull("errorCode") && body.hasNonNull("errorMessage")) {
            int code = body.get("errorCode").asInt();
            ErrorCode errorCode = ErrorCode.fromCode(code);
            HttpStatus httpStatus = HttpStatus.resolve(status);
            if (errorCode != null && httpStatus != null
                    && Integer.toString(errorCode.getCode()).startsWith(Integer.toString(httpStatus.value()))) {
                return new ApiException(httpStatus, errorCode, body.get("errorMessage").asText());
            }
        }
        if (status == HttpStatus.NOT_FOUND.value()) {
            return id == null
                    ? new ApiException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Message not found")
                    : ApiException.notFound("Message", id);
        }
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL, "Discussion service call failed");
    }

    private JsonNode readBody(RestClientResponseException exception) {
        try {
            byte[] payload = exception.getResponseBodyAsByteArray();
            if (payload.length == 0) {
                return null;
            }
            return objectMapper.readTree(payload);
        } catch (Exception ignored) {
            return null;
        }
    }

    @FunctionalInterface
    private interface RemoteCall<T> {
        T execute();
    }
}
