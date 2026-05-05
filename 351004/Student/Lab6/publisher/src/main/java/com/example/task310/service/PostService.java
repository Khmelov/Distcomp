package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.enums.PostState;
import com.example.task310.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PostService {

    private final RestClient restClient;
    private final KafkaTemplate<String, PostRequestTo> kafkaTemplate;
    private final IssueRepository issueRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "post_cache::";

    public List<PostResponseTo> getAll(Pageable pageable) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", pageable.getPageNumber())
                        .queryParam("size", pageable.getPageSize())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponseTo>>() {});
    }

    public PostResponseTo getById(Long id) {
        String key = CACHE_PREFIX + id;
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof PostResponseTo postResponseTo) {
                return postResponseTo;
            }
        } catch (Exception e) {
            System.err.println("Redis access error: " + e.getMessage());
        }
        try {
            PostResponseTo response = restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .body(PostResponseTo.class);

            if (response != null) {
                redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(10));
                return response;
            }
        } catch (Exception e) {
            throw new RuntimeException("Post not found");
        }
        throw new RuntimeException("Post not found");
    }

    public PostResponseTo create(PostRequestTo dto) {
        if (!issueRepository.existsById(dto.getIssueId())) {
            throw new RuntimeException("Issue not found");
        }

        if (dto.getId() == null) {
            dto.setId(Math.abs(ThreadLocalRandom.current().nextLong()));
        }
        dto.setState(PostState.PENDING);

        PostResponseTo pending = new PostResponseTo(
                dto.getId(),
                dto.getIssueId(),
                dto.getContent(),
                dto.getState()
        );

        redisTemplate.opsForValue().set(CACHE_PREFIX + dto.getId(), pending, Duration.ofMinutes(10));

        kafkaTemplate.send("InTopic", String.valueOf(dto.getIssueId()), dto);

        return pending;
    }

    public PostResponseTo update(PostRequestTo dto) {
        PostResponseTo updated = restClient.put()
                .uri("/{id}", dto.getId())
                .body(dto)
                .retrieve()
                .body(PostResponseTo.class);

        if (updated != null) {
            redisTemplate.opsForValue().set(CACHE_PREFIX + dto.getId(), updated, Duration.ofMinutes(10));
        }
        return updated;
    }

    public void delete(Long id) {
        redisTemplate.delete(CACHE_PREFIX + id);
        restClient.delete().uri("/{id}", id).retrieve().toBodilessEntity();
    }
}