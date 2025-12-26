package com.restApp.client;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

@Component
public class DiscussionClient {

    private final RestClient restClient;

    public DiscussionClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:24130/api/v1.0").build();
    }

    @Cacheable(value = "newsComments", key = "#newsId")
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

    @Cacheable(value = "comments", key = "#id")
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

    @CachePut(value = "comments", key = "#id")
    @CacheEvict(value = "newsComments", key = "#request.newsId")
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        return restClient.put()
                .uri("/comments/{id}", id)
                .body(request)
                .retrieve()
                .body(CommentResponseTo.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "comments", key = "#id"),
            @CacheEvict(value = "newsComments", allEntries = true)
    })
    public void delete(Long id) {
        restClient.delete()
                .uri("/comments/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    @CachePut(value = "comments", key = "#result.id()")
    @CacheEvict(value = "newsComments", key = "#request.newsId")
    public CommentResponseTo create(CommentRequestTo request) {
        return restClient.post()
                .uri("/comments")
                .body(request)
                .retrieve()
                .body(CommentResponseTo.class);
    }
}
