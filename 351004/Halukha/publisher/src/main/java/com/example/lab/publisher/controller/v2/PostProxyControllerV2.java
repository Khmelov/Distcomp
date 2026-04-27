package com.example.lab.publisher.controller.v2;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.lab.publisher.exception.GlobalExceptionHandler.ErrorResponse;
import com.example.lab.publisher.security.OwnershipService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2.0/posts")
public class PostProxyControllerV2 {

    private final WebClient webClient;
    private final OwnershipService ownership;

    public PostProxyControllerV2(WebClient.Builder webClientBuilder, OwnershipService ownership) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:24130").build();
        this.ownership = ownership;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public Mono<ResponseEntity<Object>> createPost(@RequestBody Object post) {
        Long newsId = extractNewsId(post);
        if (!canWrite(newsId)) {
            return Mono.just(ResponseEntity.status(403).body(new ErrorResponse(40301, "Forbidden")));
        }
        return webClient.post()
                .uri("/api/v1.0/posts")
                .bodyValue(post)
                .retrieve()
                .toEntity(Object.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    Object errorBody = e.getResponseBodyAs(Object.class);
                    return Mono.just(ResponseEntity.status(e.getStatusCode()).body(errorBody));
                });
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public Mono<ResponseEntity<Object>> updatePost(@PathVariable Long id, @RequestBody Object post) {
        Long newsId = extractNewsId(post);
        if (!canWrite(newsId)) {
            return Mono.just(ResponseEntity.status(403).body(new ErrorResponse(40301, "Forbidden")));
        }
        return webClient.put()
                .uri("/api/v1.0/posts/{id}", id)
                .bodyValue(post)
                .exchangeToMono(response -> response.toEntity(Object.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable Long id) {
        return webClient.get()
                .uri("/api/v1.0/posts/{id}", id)
                .exchangeToMono(response -> response.toEntity(Object.class))
                .flatMap(entity -> {
                    Long newsId = extractNewsId(entity.getBody());
                    if (!canWrite(newsId)) {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                    return webClient.delete()
                            .uri("/api/v1.0/posts/{id}", id)
                            .exchangeToMono(resp -> resp.toBodilessEntity());
                });
    }

    private boolean canWrite(Long newsId) {
        if (newsId == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        return ownership.canModifyNews(newsId);
    }

    private boolean isAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) {
            return false;
        }
        return a.getAuthorities().stream().anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
    }

    private Long extractNewsId(Object body) {
        if (body instanceof Map<?, ?> map) {
            Object v = map.get("newsId");
            if (v instanceof Number n) {
                return n.longValue();
            }
        }
        return null;
    }
}

