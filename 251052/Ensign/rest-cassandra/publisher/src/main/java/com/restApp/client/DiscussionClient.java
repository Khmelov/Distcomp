package com.restApp.client;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@Component
public class DiscussionClient {

    private final RestClient restClient;

    public DiscussionClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:24130/api/v1.0").build();
    }

    public List<CommentResponseTo> getCommentsByNewsId(Long newsId) {
        try {
            return restClient.get()
                    .uri("/comments/news/{newsId}", newsId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CommentResponseTo>>() {
                    });
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<CommentResponseTo> findAll() {
        try {
            return restClient.get()
                    .uri("/comments")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CommentResponseTo>>() {
                    });
        } catch (Exception e) {
            return List.of();
        }
    }

    public CommentResponseTo findById(Long id) {
        try {
            return restClient.get()
                    .uri("/comments/{id}", id)
                    .retrieve()
                    .body(CommentResponseTo.class);
        } catch (Exception e) {
            return null;
        }
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        return restClient.put()
                .uri("/comments/{id}", id)
                .body(request)
                .retrieve()
                .body(CommentResponseTo.class);
    }

    public void delete(Long id) {
        restClient.delete()
                .uri("/comments/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public CommentResponseTo create(CommentRequestTo request) {
        return restClient.post()
                .uri("/comments")
                .body(request)
                .retrieve()
                .body(CommentResponseTo.class);
    }
}
