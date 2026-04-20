package com.example.restApi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final WebClient webClient;

    public CommentController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public ResponseEntity<List> getAll() {
        List result = webClient.get()
                .uri("/api/v1.0/comments")
                .retrieve()
                .bodyToMono(List.class)
                .block();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        Object result = webClient.get()
                .uri("/api/v1.0/comments/" + id)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Object request) {
        Object result = webClient.post()
                .uri("/api/v1.0/comments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody Object request) {
        Object result = webClient.put()
                .uri("/api/v1.0/comments/" + id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        webClient.delete()
                .uri("/api/v1.0/comments/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return ResponseEntity.noContent().build();
    }
}