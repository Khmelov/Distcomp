package org.example.newsapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentProxyController {

    private final WebClient discussionWebClient;

    @GetMapping
    public Mono<ResponseEntity<String>> getAll() {
        return discussionWebClient
                .method(HttpMethod.GET)
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getById(@PathVariable Long id) {
        return discussionWebClient
                .method(HttpMethod.GET)
                .uri("/{id}", id)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        return discussionWebClient
                .method(HttpMethod.POST)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> update(@PathVariable Long id, @RequestBody String body) {
        return discussionWebClient
                .method(HttpMethod.PUT)
                .uri("/{id}", id)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable Long id) {
        return discussionWebClient
                .method(HttpMethod.DELETE)
                .uri("/{id}", id)
                .retrieve()
                .toEntity(String.class);
    }
}