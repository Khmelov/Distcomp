package com.distcomp.client;

import com.distcomp.common.notice.NoticeDto;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.exception.ErrorResponse;
import com.distcomp.exception.RemoteDiscussionException;
import com.distcomp.pagination.PagedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * REST access to the discussion module on port 24130.
 *
 * <p>The Kafka exchange is the primary transport. This client is kept for two
 * reasons: it satisfies the REST-based variant of the modularisation task, and
 * it allows the publisher to keep working when the broker is unavailable.</p>
 */
@Component
public class DiscussionNoticeClient {

    private static final String NOTICES = "/api/v1.0/notices";

    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public DiscussionNoticeClient(RestClient discussionRestClient, ObjectMapper objectMapper) {
        this.restClient = discussionRestClient;
        this.objectMapper = objectMapper;
    }

    public NoticeDto create(NoticeRequestTo request) {
        return errors(restClient.post()
                .uri(NOTICES)
                .body(request)
                .retrieve())
                .body(NoticeDto.class);
    }

    public NoticeDto update(Long id, NoticeRequestTo request) {
        NoticeRequestTo payload = new NoticeRequestTo(id, request.issueId(), request.content(), null);
        return errors(restClient.put()
                .uri(NOTICES)
                .body(payload)
                .retrieve())
                .body(NoticeDto.class);
    }

    public NoticeDto findById(Long id) {
        return errors(restClient.get()
                .uri(NOTICES + "/{id}", id)
                .retrieve())
                .body(NoticeDto.class);
    }

    public PagedResult<NoticeDto> findAll(Long issueId, Integer page, Integer size, String sort) {
        ResponseEntity<List<NoticeDto>> entity = errors(restClient.get()
                .uri(builder -> builder.path(NOTICES)
                        .queryParamIfPresent("issueId", Optional.ofNullable(issueId))
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .queryParamIfPresent("sort", Optional.ofNullable(sort))
                        .build())
                .retrieve())
                .toEntity(new ParameterizedTypeReference<List<NoticeDto>>() {
                });

        List<NoticeDto> body = entity.getBody() == null ? List.of() : entity.getBody();
        String totalHeader = entity.getHeaders().getFirst("X-Total-Count");
        long total = totalHeader == null ? body.size() : Long.parseLong(totalHeader);
        return new PagedResult<>(body, total);
    }

    public void delete(Long id) {
        errors(restClient.delete()
                .uri(NOTICES + "/{id}", id)
                .retrieve())
                .toBodilessEntity();
    }

    public void deleteByIssueId(Long issueId) {
        errors(restClient.delete()
                .uri(NOTICES + "/by-issue/{issueId}", issueId)
                .retrieve())
                .toBodilessEntity();
    }

    private RestClient.ResponseSpec errors(RestClient.ResponseSpec spec) {
        return spec.onStatus(HttpStatusCode::isError, (request, response) -> {
            String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            ErrorResponse error = parse(body);
            throw new RemoteDiscussionException(
                    HttpStatus.valueOf(response.getStatusCode().value()),
                    error.errorCode(),
                    error.errorMessage());
        });
    }

    private ErrorResponse parse(String body) {
        try {
            ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
            if (error.errorCode() != null && error.errorMessage() != null) {
                return error;
            }
        } catch (Exception ignored) {
            // fall through to the generic error below
        }
        return new ErrorResponse("Discussion service returned an invalid error response", "50301");
    }
}
