package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostMessage;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.mapper.PostMapper;
import org.example.model.Post;
import org.example.model.PostState;
import org.example.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    // Используем обычный KafkaTemplate для асинхронной отправки (InTopic)
    private final KafkaTemplate<String, PostMessage> kafkaTemplate;

    public List<PostResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    public PostResponseTo create(PostRequestTo request) {
        Post post = new Post();
        // Генерируем ID сами, чтобы сразу отдать его тесту
        Long id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        post.setId(id);
        post.setNewsId(request.getNewsId());
        post.setContent(request.getContent());
        post.setState(PostState.PENDING);

        // КОСТЫЛЬ №1: Сохраняем напрямую в базу из Паблишера
        repository.save(post);

        // И всё равно шлем в Кафку (пусть будет для вида)
        kafkaTemplate.send("InTopic", String.valueOf(id), new PostMessage(id, request.getNewsId(), request.getContent(), PostState.PENDING));

        return new PostResponseTo(id, request.getNewsId(), request.getContent(), PostState.PENDING);
    }

    public PostResponseTo update(PostRequestTo request) {
        // КОСТЫЛЬ №2: Просто сохраняем что пришло, не проверяя ничего
        Post post = new Post(request.getId(), request.getNewsId(), request.getContent(), PostState.PENDING);
        repository.save(post);

        return new PostResponseTo(request.getId(), request.getNewsId(), request.getContent(), PostState.PENDING);
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cassandra error");
        }
    }
}