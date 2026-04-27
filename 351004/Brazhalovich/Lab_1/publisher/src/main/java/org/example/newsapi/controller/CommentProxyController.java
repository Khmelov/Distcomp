package org.example.newsapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentProxyController {

    private final WebClient discussionWebClient;

    private boolean isValidId(String id) {
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getAll() {
        log.info("Proxying GET /comments");
        return discussionWebClient
                .get()
                .uri("/api/v1.0/comments")
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getById(@PathVariable String id) {
        log.info("Proxying GET /comments/{}", id);
        if (!isValidId(id)) {
            return Mono.just(ResponseEntity.ok("{}"));
        }
        return discussionWebClient
                .get()
                .uri("/api/v1.0/comments/{id}", id)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        log.info("Proxying POST /comments");
        return discussionWebClient
                .post()
                .uri("/api/v1.0/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> update(@PathVariable String id, @RequestBody String body) {
        log.info("Proxying PUT /comments/{}", id);
        if (!isValidId(id)) {
            return Mono.just(ResponseEntity.ok("{}"));
        }
        return discussionWebClient
                .put()
                .uri("/api/v1.0/comments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        log.info("Proxying DELETE /comments/{}", id);
        if (!isValidId(id)) {
            return Mono.just(ResponseEntity.noContent().build());
        }
        return discussionWebClient
                .delete()
                .uri("/api/v1.0/comments/{id}", id)
                .retrieve()
                .toEntity(String.class);
    }
}