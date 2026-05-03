package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.dto.response.ErrorResponseTo;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.service.CommentService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * REST-based implementation (not registered as a Spring bean; {@link CommentServiceKafkaImpl} is {@code @Primary}).
 */
public class CommentServiceRemoteImpl implements CommentService {

    private static final String COMMENTS_PATH = "/api/v1.0/comments";

    private final RestClient discussionRestClient;

    public CommentServiceRemoteImpl(RestClient discussionRestClient) {
        this.discussionRestClient = discussionRestClient;
    }

    @Override
    public List<CommentResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long tweetId, String content) {
        return discussionRestClient.get()
                .uri(uriBuilder -> {
                    var b = uriBuilder.path(COMMENTS_PATH)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParam("sortBy", sortBy)
                            .queryParam("sortDir", sortDir);
                    if (tweetId != null) {
                        b.queryParam("tweetId", tweetId);
                    }
                    if (content != null && !content.isBlank()) {
                        b.queryParam("content", content);
                    }
                    return b.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseTo>>() {
                });
    }

    @Override
    public CommentResponseTo findById(Long id) {
        try {
            return discussionRestClient.get()
                    .uri(COMMENTS_PATH + "/{id}", id)
                    .retrieve()
                    .body(CommentResponseTo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Comment", id);
        }
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        try {
            return discussionRestClient.post()
                    .uri(COMMENTS_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CommentResponseTo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw mapNotFoundForCreate(ex, request.getTweetId());
        }
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        try {
            return discussionRestClient.put()
                    .uri(COMMENTS_PATH + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CommentResponseTo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw mapNotFoundForUpdate(ex, request.getTweetId(), id);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            discussionRestClient.delete()
                    .uri(COMMENTS_PATH + "/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Comment", id);
        }
    }

    private RuntimeException mapNotFoundForCreate(HttpClientErrorException.NotFound ex, Long tweetId) {
        ErrorResponseTo body = ex.getResponseBodyAs(ErrorResponseTo.class);
        if (body != null && body.getErrorMessage() != null && body.getErrorMessage().contains("Tweet")) {
            return new EntityNotFoundException("Tweet", tweetId);
        }
        throw ex;
    }

    private RuntimeException mapNotFoundForUpdate(HttpClientErrorException.NotFound ex, Long tweetId, Long commentId) {
        ErrorResponseTo body = ex.getResponseBodyAs(ErrorResponseTo.class);
        if (body != null && body.getErrorMessage() != null && body.getErrorMessage().contains("Tweet")) {
            return new EntityNotFoundException("Tweet", tweetId);
        }
        throw new EntityNotFoundException("Comment", commentId);
    }
}
