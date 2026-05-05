package org.example.newsapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentProxyController {

    private final WebClient discussionWebClient;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate; // для работы со строками

    private static final String CACHE_KEY_PREFIX = "comment::";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

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
        return discussionWebClient.get()
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

        String cacheKey = CACHE_KEY_PREFIX + id;
        // 1. Пытаемся получить из Redis
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .flatMap(cachedJson -> {
                    log.info("Cache hit for comment id: {}", id);
                    // Возвращаем ResponseEntity с телом и статусом 200 из кеша
                    return Mono.just(ResponseEntity.ok(cachedJson));
                })
                .switchIfEmpty(
                        // 2. Нет в кеше – запрос к discussion
                        discussionWebClient.get()
                                .uri("/api/v1.0/comments/{id}", id)
                                .retrieve()
                                .toEntity(String.class)
                                .flatMap(response -> {
                                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                        // Сохраняем в Redis только успешные ответы (body как строку)
                                        return reactiveRedisTemplate.opsForValue()
                                                .set(cacheKey, response.getBody(), CACHE_TTL)
                                                .thenReturn(response);
                                    }
                                    return Mono.just(response);
                                })
                );
    }

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody String body) {
        log.info("Proxying POST /comments");
        // При создании комментария через publisher инвалидируем кеш (поскольку он мог измениться)
        return reactiveRedisTemplate.keys(CACHE_KEY_PREFIX + "*")
                .flatMap(reactiveRedisTemplate::delete)
                .then(
                        discussionWebClient.post()
                                .uri("/api/v1.0/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(String.class)
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> update(@PathVariable String id, @RequestBody String body) {
        log.info("Proxying PUT /comments/{}", id);
        if (!isValidId(id)) {
            return Mono.just(ResponseEntity.ok("{}"));
        }
        // Обновление через publisher – инвалидируем кеш конкретного комментария
        String cacheKey = CACHE_KEY_PREFIX + id;
        return reactiveRedisTemplate.delete(cacheKey)
                .then(
                        discussionWebClient.put()
                                .uri("/api/v1.0/comments/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(String.class)
                );
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        log.info("Proxying DELETE /comments/{}", id);
        if (!isValidId(id)) {
            return Mono.just(ResponseEntity.noContent().build());
        }
        String cacheKey = CACHE_KEY_PREFIX + id;
        return reactiveRedisTemplate.delete(cacheKey)
                .then(
                        discussionWebClient.delete()
                                .uri("/api/v1.0/comments/{id}", id)
                                .retrieve()
                                .toEntity(String.class)
                );
    }
}