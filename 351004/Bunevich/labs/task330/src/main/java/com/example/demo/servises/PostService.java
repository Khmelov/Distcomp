package com.example.demo.servises;

import com.example.demo.dto.request.PostRequestTo;
import com.example.demo.dto.response.PostResponseTo;
import com.example.demo.exeptionHandler.ResourceNotFoundException;
import com.example.demo.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class PostService {
    private final RestClient.Builder restClientBuilder;
    private final StoryRepository storyRepository;

    @Value("${discussion.base-url:http://localhost:24130}")
    private String discussionBaseUrl;

    public PostService(RestClient.Builder restClientBuilder, StoryRepository storyRepository) {
        this.restClientBuilder = restClientBuilder;
        this.storyRepository = storyRepository;
    }

    public List<PostResponseTo> getPosts(int page, int size, String sortBy, String sortDir, String content) {
        return discussionClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1.0/posts")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("sortBy", sortBy)
                        .queryParam("sortDir", sortDir)
                        .queryParamIfPresent("content", content == null || content.isBlank()
                                ? java.util.Optional.empty()
                                : java.util.Optional.of(content))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public PostResponseTo create(PostRequestTo postRequestTo) {
        validateStoryExists(postRequestTo.storyId);
        return discussionClient()
                .post()
                .uri("/api/v1.0/posts")
                .body(postRequestTo)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public void delete(Long id) {
        try {
            discussionClient()
                    .delete()
                    .uri("/api/v1.0/posts/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Post", id);
        }
    }

    public PostResponseTo findById(Long id) {
        try {
            return discussionClient()
                    .get()
                    .uri("/api/v1.0/posts/{id}", id)
                    .retrieve()
                    .body(PostResponseTo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Post", id);
        }
    }

    public PostResponseTo update(Long id, PostRequestTo postRequestTo) {
        validateStoryExists(postRequestTo.storyId);
        try {
            return discussionClient()
                    .put()
                    .uri("/api/v1.0/posts/{id}", id)
                    .body(postRequestTo)
                    .retrieve()
                    .body(PostResponseTo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Post", id);
        }
    }

    private RestClient discussionClient() {
        return restClientBuilder.baseUrl(discussionBaseUrl).build();
    }

    private void validateStoryExists(Long storyId) {
        if (storyId == null || !storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("Story", storyId);
        }
    }
}