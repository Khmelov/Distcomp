package com.example.lab.publisher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostProxyController {

    private final WebClient webClient;

    public PostProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:24130").build();
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> getAllPosts() {
        return webClient.get()
                .uri("/api/v1.0/posts")
                .exchangeToMono(response -> response.toEntity(Object.class));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getPostById(@PathVariable Long id) {
        return webClient.get()
                .uri("/api/v1.0/posts/{id}", id)
                .exchangeToMono(response -> response.toEntity(Object.class));
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> createPost(@RequestBody Object post) {
        return webClient.post()
                .uri("/api/v1.0/posts")
                .bodyValue(post)
                .retrieve()
                .toEntity(Object.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    Object errorBody = e.getResponseBodyAs(Object.class);
                    return Mono.just(ResponseEntity
                            .status(e.getStatusCode())
                            .body(errorBody));
                });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> updatePost(@PathVariable Long id, @RequestBody Object post) {
        return webClient.put()
                .uri("/api/v1.0/posts/{id}", id)
                .bodyValue(post)
                .exchangeToMono(response -> response.toEntity(Object.class));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable Long id) {
        return webClient.delete()
                .uri("/api/v1.0/posts/{id}", id)
                .exchangeToMono(response -> response.toBodilessEntity());
    }
}