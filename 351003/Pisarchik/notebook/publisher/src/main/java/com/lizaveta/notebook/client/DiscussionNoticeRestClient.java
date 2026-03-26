package com.lizaveta.notebook.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lizaveta.notebook.exception.ErrorResponse;
import com.lizaveta.notebook.exception.ForbiddenException;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.exception.ValidationException;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DiscussionNoticeRestClient implements DiscussionNoticeClient {

    private static final ParameterizedTypeReference<List<NoticeResponseTo>> NOTICE_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<PageResponseTo<NoticeResponseTo>> NOTICE_PAGE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DiscussionNoticeRestClient(
            @Qualifier("discussion") final RestClient restClient,
            final ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public NoticeResponseTo create(final NoticeRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NoticeResponseTo.class);
    }

    @Override
    public List<NoticeResponseTo> findAllAsList() {
        return restClient.get()
                .uri("/api/v1.0/notices")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NOTICE_LIST_TYPE);
    }

    @Override
    public PageResponseTo<NoticeResponseTo> findAllPaged(
            final int page,
            final int size,
            final String sortBy,
            final String sortOrder) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/api/v1.0/notices")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sortOrder", sortOrder == null ? "asc" : sortOrder);
        if (sortBy != null && !sortBy.isBlank()) {
            builder.queryParam("sortBy", sortBy);
        }
        String path = builder.build().toUriString();
        return restClient.get()
                .uri(path)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NOTICE_PAGE_TYPE);
    }

    @Override
    public NoticeResponseTo findById(final Long id) {
        return restClient.get()
                .uri("/api/v1.0/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NoticeResponseTo.class);
    }

    @Override
    public List<NoticeResponseTo> findByStoryId(final Long storyId) {
        return restClient.get()
                .uri("/api/v1.0/notices/by-story/{storyId}", storyId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NOTICE_LIST_TYPE);
    }

    @Override
    public NoticeResponseTo update(final Long id, final NoticeRequestTo request) {
        return restClient.put()
                .uri("/api/v1.0/notices/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .body(NoticeResponseTo.class);
    }

    @Override
    public void deleteById(final Long id) {
        restClient.delete()
                .uri("/api/v1.0/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw mapError(res);
                })
                .toBodilessEntity();
    }

    private RuntimeException mapError(final org.springframework.http.client.ClientHttpResponse response) {
        try (InputStream body = response.getBody()) {
            if (body == null) {
                return new ValidationException("Discussion service error", 50001);
            }
            String raw = new String(body.readAllBytes(), StandardCharsets.UTF_8);
            if (raw.isBlank()) {
                return new ValidationException("Discussion service error", 50001);
            }
            ErrorResponse err = objectMapper.readValue(raw, ErrorResponse.class);
            int status = response.getStatusCode().value();
            if (status == 404) {
                return new ResourceNotFoundException(err.errorMessage());
            }
            if (status == 400) {
                return new ValidationException(err.errorMessage(), err.errorCode());
            }
            if (status == 403) {
                return new ForbiddenException(err.errorMessage());
            }
            return new ValidationException(err.errorMessage(), err.errorCode());
        } catch (IOException ex) {
            return new ValidationException("Failed to read discussion error response", 50001);
        }
    }
}
