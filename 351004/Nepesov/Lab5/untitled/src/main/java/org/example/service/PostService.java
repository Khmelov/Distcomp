package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PostMessage;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.mapper.PostMapper;
import org.example.model.Post;
import org.example.model.PostState;
import org.example.repository.PostRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final KafkaTemplate<String, PostMessage> kafkaTemplate;

    public List<PostResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "posts", key = "#id")
    public PostResponseTo findById(String id) {
        Long numericId = parseIdSafely(id);
        log.info("Searching for post ID: {} (parsed as: {})", id, numericId);

        return repository.findById(numericId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @CachePut(value = "posts", key = "#result.id.toString()")
    public PostResponseTo create(PostRequestTo request) {

        Long numericId = (request.getId() != null) ? parseIdSafely(request.getId()) :
                ThreadLocalRandom.current().nextLong(1, 2000000000000L);

        Long numericNewsId = parseIdSafely(request.getNewsId());

        Post post = new Post(numericId, numericNewsId, request.getContent(), PostState.PENDING);
        repository.save(post);

        try {
            kafkaTemplate.send("InTopic", String.valueOf(numericId),
                    new PostMessage(numericId, numericNewsId, request.getContent(), PostState.PENDING));
        } catch (Exception ignore) {}

        return mapper.toResponse(post);
    }

    @CachePut(value = "posts", key = "#request.id.toString()")
    public PostResponseTo update(PostRequestTo request) {
        Long numericId = parseIdSafely(request.getId());
        Long numericNewsId = parseIdSafely(request.getNewsId());

        Post post = new Post(numericId, numericNewsId, request.getContent(), PostState.PENDING);
        repository.save(post);

        return mapper.toResponse(post);
    }

    @CacheEvict(value = "posts", key = "#id")
    public void delete(String id) {
        repository.deleteById(parseIdSafely(id));
    }

    private Long parseIdSafely(Object id) {
        if (id == null) return 0L;
        String s = String.valueOf(id).trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0L;
        try {
            // Исправленная логика для работы с наутационной записью (1.23E10)
            if (s.contains("E") || s.contains(".") || s.contains("+")) {
                return Double.valueOf(s).longValue();
            }
            return Long.parseLong(s);
        } catch (Exception e) {
            log.error("Failed to parse ID: {}", s);
            return 0L;
        }
    }

    @CachePut(value = "posts", key = "#message.id.toString()")
    public PostResponseTo updateFromKafka(PostMessage message) {
        log.info("Updating Redis and DB from Kafka for ID: {}", message.getId());

        Post post = new Post();
        post.setId(message.getId());
        post.setNewsId(message.getNewsId());
        post.setContent(message.getContent());
        post.setState(message.getState());

        repository.save(post);
        return mapper.toResponse(post);
    }
}