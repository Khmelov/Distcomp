package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.PostRequestTo;
import com.example.entitiesapp.dto.response.PostResponseTo;
import com.example.entitiesapp.entities.Article;
import com.example.entitiesapp.entities.Post;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.PostMapper;
import com.example.entitiesapp.repositories.ArticleRepository;
import com.example.entitiesapp.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ArticleRepository articleRepository;
    private final PostMapper postMapper;

    public List<PostResponseTo> getAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PostResponseTo getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return postMapper.toResponseDto(post);
    }

    public PostResponseTo create(PostRequestTo dto) {
        validatePostRequest(dto);

        // Проверяем существование article
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + dto.getArticleId()));

        Post post = postMapper.toEntity(dto);
        post.setCreated(LocalDateTime.now());
        post.setModified(LocalDateTime.now());

        Post saved = postRepository.save(post);
        return postMapper.toResponseDto(saved);
    }

    public PostResponseTo update(Long id, PostRequestTo dto) {
        validatePostRequest(dto);

        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Проверяем существование article
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + dto.getArticleId()));

        Post post = postMapper.toEntity(dto);
        post.setId(id);
        post.setCreated(existing.getCreated());
        post.setModified(LocalDateTime.now());

        Post updated = postRepository.update(post);
        return postMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    private void validatePostRequest(PostRequestTo dto) {
        if (dto.getArticleId() == null) {
            throw new ValidationException("articleId is required");
        }
        if (dto.getContent() == null || dto.getContent().length() < 4 || dto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 4 and 2048 characters");
        }
    }
}