package com.example.discussion.client;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PostClient {

    private final RestClient restClient;
    private final String baseUrl;

    public PostClient(@Value("${discussion.service.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new RuntimeException("Discussion service error: " + response.getStatusCode());
                        })
                .build();
    }

    public List<PostResponseTo> getAllPosts() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public PostResponseTo getPostById(Long id) {
        return restClient.get()
                .uri(baseUrl + "/{id}", id)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public PostResponseTo createPost(PostRequestTo dto) {
        return restClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public PostResponseTo updatePost(PostRequestTo dto) {
        return restClient.put()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(PostResponseTo.class);
    }

    public void deletePost(Long id) {
        restClient.delete()
                .uri(baseUrl + "/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}