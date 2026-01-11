package com.distcomp.publisher.post.client;

import com.distcomp.publisher.post.dto.PostRequest;
import com.distcomp.publisher.post.dto.PostResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class DiscussionPostClient {

    private final RestClient restClient;

    public DiscussionPostClient(RestClient discussionRestClient) {
        this.restClient = discussionRestClient;
    }

    public PostResponse create(PostRequest request) {
        return restClient.post()
                .uri("/api/v1.0/posts")
                .body(request)
                .retrieve()
                .body(PostResponse.class);
    }

    public List<PostResponse> listAll() {
        return restClient.get()
                .uri("/api/v1.0/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponse>>() {
                });
    }

    public PostResponse getById(Long id) {
        try {
            return restClient.get()
                    .uri("/api/v1.0/posts/{id}", id)
                    .retrieve()
                    .body(PostResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public PostResponse updateById(Long id, PostRequest request) {
        try {
            return restClient.put()
                    .uri("/api/v1.0/posts/{id}", id)
                    .body(request)
                    .retrieve()
                    .body(PostResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public boolean deleteById(Long id) {
        try {
            restClient.delete()
                    .uri("/api/v1.0/posts/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }
}
