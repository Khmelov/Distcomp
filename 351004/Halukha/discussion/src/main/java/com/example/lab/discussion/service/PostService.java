package com.example.lab.discussion.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.lab.discussion.dto.PostRequestTo;
import com.example.lab.discussion.dto.PostResponseTo;
import com.example.lab.discussion.exception.EntityNotFoundException;
import com.example.lab.discussion.mapper.PostMapper;
import com.example.lab.discussion.model.Post;
import com.example.lab.discussion.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper mapper = PostMapper.INSTANCE;

    private final WebClient webClient;

    public PostService(PostRepository postRepository, WebClient.Builder webClientBuilder) {
        this.postRepository = postRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:24110").build();
    }

    public List<PostResponseTo> getAllPost() {
        return postRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PostResponseTo getPostById(Long id) {
        return postRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
    }

    public PostResponseTo createPost(PostRequestTo request) {
        try {
            Boolean exists = webClient.get()
                    .uri("/api/v1.0/news/{id}", request.getNewsId())
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .block();

            if (exists == null || !exists) {
                throw new EntityNotFoundException("News not found", 40401);
            }
        } catch (Exception e) {
            throw new EntityNotFoundException("News not found or service unavailable", 40401);
        }

        Post post = new Post();
        post.setId(System.currentTimeMillis());
        post.setNewsId(request.getNewsId());
        post.setContent(request.getContent());

        return mapper.toDto(postRepository.save(post));
    }

    public PostResponseTo updatePost(Long id, PostRequestTo request) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
        Post updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        Post saved = postRepository.save(updated);
        return mapper.toDto(saved);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found", 40401);
        }
        postRepository.deleteById(id);
    }
}
