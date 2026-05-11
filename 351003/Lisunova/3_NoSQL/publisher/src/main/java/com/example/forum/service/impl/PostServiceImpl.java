package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.repository.TopicRepository;
import com.example.forum.service.PostService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import com.example.forum.dto.RestPageImpl;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final RestClient postClient;
    private final TopicRepository topicRepository;

    public PostServiceImpl(RestClient postClient, TopicRepository topicRepository) {
        this.postClient = postClient;
        this.topicRepository = topicRepository;
    }

    @Override
    public PostResponseTo create(PostRequestTo request) {
        validate(request);

        // Проверяем, существует ли топик в нашей БД (Postgres)
        if (!topicRepository.existsById(request.getTopicId())) {
            throw new NotFoundException("Topic not found", "40434");
        }

        // Отправляем запрос в модуль discussion
        return postClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Post service error", "40431");
                })
                .body(PostResponseTo.class);
    }

    @Override
    public PostResponseTo getById(Long id) {
        return postClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Post not found in discussion service", "40431");
                })
                .body(PostResponseTo.class);
    }

    @Override
    public Page<PostResponseTo> getAll(Long topicId, Pageable pageable) {
        // 1. Получаем List вместо RestPageImpl
        List<PostResponseTo> posts = postClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("topicId", topicId)
                        // Параметры page/size можно оставить,
                        // даже если discussion их игнорирует, это не сломает запрос
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PostResponseTo>>() {});

        // 2. Оборачиваем полученный список в PageImpl,
        // чтобы не ломать логику в publisher, которая ожидает Page
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public PostResponseTo update(Long id, PostRequestTo request) {
        validate(request);

        if (!topicRepository.existsById(request.getTopicId())) {
            throw new NotFoundException("Topic not found", "40434");
        }

        return postClient.put()
                .uri("/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PostResponseTo.class);
    }

    @Override
    public void delete(Long id) {
        postClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new NotFoundException("Post not found", "40431");
                })
                .toBodilessEntity();
    }

    private void validate(PostRequestTo request) {
        if (request.getTopicId() == null) {
            throw new BadRequestException("TopicId is required", "40031");
        }
        if (!StringUtils.hasText(request.getContent()) ||
                request.getContent().length() < 2 ||
                request.getContent().length() > 2048) {
            throw new BadRequestException("Invalid content", "40032");
        }
    }
}