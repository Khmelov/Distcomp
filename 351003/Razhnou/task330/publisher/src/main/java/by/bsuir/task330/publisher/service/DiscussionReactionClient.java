package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task330.publisher.dto.response.ErrorResponse;
import by.bsuir.task330.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task330.publisher.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class DiscussionReactionClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DiscussionReactionClient(RestClient discussionRestClient, ObjectMapper objectMapper) {
        this.restClient = discussionRestClient;
        this.objectMapper = objectMapper;
    }

    public boolean isAvailable() {
        try {
            restClient.get()
                    .uri("/api/v1.0/reactions")
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public ReactionResponseTo create(ReactionRequestTo request) {
        try {
            return restClient.post()
                    .uri("/api/v1.0/reactions")
                    .body(request)
                    .retrieve()
                    .body(ReactionResponseTo.class);
        } catch (RestClientResponseException exception) {
            throw mapRemoteException(exception);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Discussion service is unavailable");
        }
    }

    public List<ReactionResponseTo> findAll() {
        try {
            return restClient.get()
                    .uri("/api/v1.0/reactions")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ReactionResponseTo>>() {});
        } catch (RestClientResponseException exception) {
            throw mapRemoteException(exception);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Discussion service is unavailable");
        }
    }

    public ReactionResponseTo findById(Long id) {
        try {
            return restClient.get()
                    .uri("/api/v1.0/reactions/{id}", id)
                    .retrieve()
                    .body(ReactionResponseTo.class);
        } catch (RestClientResponseException exception) {
            throw mapRemoteException(exception);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Discussion service is unavailable");
        }
    }

    public ReactionResponseTo update(ReactionRequestTo request) {
        try {
            return restClient.put()
                    .uri("/api/v1.0/reactions")
                    .body(request)
                    .retrieve()
                    .body(ReactionResponseTo.class);
        } catch (RestClientResponseException exception) {
            throw mapRemoteException(exception);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Discussion service is unavailable");
        }
    }

    public void delete(Long id) {
        try {
            restClient.delete()
                    .uri("/api/v1.0/reactions/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw mapRemoteException(exception);
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Discussion service is unavailable");
        }
    }

    private ApiException mapRemoteException(RestClientResponseException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        HttpStatus resolvedStatus = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        try {
            ErrorResponse errorResponse = objectMapper.readValue(exception.getResponseBodyAsByteArray(), ErrorResponse.class);
            return new ApiException(resolvedStatus, errorResponse.errorCode(), errorResponse.errorMessage());
        } catch (Exception parseException) {
            String message = exception.getResponseBodyAsString();
            if (message == null || message.isBlank()) {
                message = exception.getStatusText();
            }
            return new ApiException(resolvedStatus, resolvedStatus.value() * 100 + 1, message);
        }
    }
}
