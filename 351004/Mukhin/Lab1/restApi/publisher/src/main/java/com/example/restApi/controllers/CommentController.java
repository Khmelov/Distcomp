package com.example.restApi.controllers;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.kafka.CommentKafkaMessage;
import com.example.restApi.kafka.KafkaConsumerService;
import com.example.restApi.kafka.KafkaProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final Map<Long, Object> cache = new ConcurrentHashMap<>();

    private final WebClient webClient;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;

    public CommentController(WebClient webClient,
                             KafkaProducerService kafkaProducerService,
                             KafkaConsumerService kafkaConsumerService) {
        this.webClient = webClient;
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @GetMapping
    public ResponseEntity<List> getAll() {
        List result = webClient.get().uri("/api/v1.0/comments")
                .retrieve().bodyToMono(List.class).block();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        Object cached = cache.get(id);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        try {
            Object result = webClient.get().uri("/api/v1.0/comments/" + id)
                    .retrieve().bodyToMono(Object.class).block();
            cache.put(id, result);
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CommentRequestTo request) {
        Object result = webClient.post()
                .uri("/api/v1.0/comments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        if (result instanceof java.util.Map map) {
            Long id = toLong(map.get("id"));
            Long articleId = toLong(map.get("articleId"));
            String content = (String) map.get("content");
            if (id != null) {
                cache.put(id, result);
                CommentKafkaMessage message = new CommentKafkaMessage(
                        id, articleId, content, "PENDING",
                        CommentKafkaMessage.Operation.CREATE);
                kafkaProducerService.sendToInTopic(message);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    public ResponseEntity<Object> updateWithoutId(@RequestBody java.util.Map<String, Object> request) {
        Object idObj = request.get("id");
        if (idObj == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }
        Long id = toLong(idObj);

        Object result = webClient.put()
                .uri("/api/v1.0/comments/" + id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        cache.put(id, result);

        Long articleId = toLong(request.get("articleId"));
        String content = (String) request.get("content");
        CommentKafkaMessage message = new CommentKafkaMessage(
                id, articleId, content, "PENDING",
                CommentKafkaMessage.Operation.UPDATE);
        kafkaProducerService.sendToInTopic(message);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody CommentRequestTo request) {
        Object result = webClient.put()
                .uri("/api/v1.0/comments/" + id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        cache.put(id, result);

        CommentKafkaMessage message = new CommentKafkaMessage(
                id, request.getArticleId(), request.getContent(), "PENDING",
                CommentKafkaMessage.Operation.UPDATE);
        kafkaProducerService.sendToInTopic(message);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        webClient.delete()
                .uri("/api/v1.0/comments/" + id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        cache.remove(id);

        CommentKafkaMessage message = new CommentKafkaMessage(
                id, 0L, null, null,
                CommentKafkaMessage.Operation.DELETE);
        kafkaProducerService.sendToInTopic(message);

        return ResponseEntity.noContent().build();
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
