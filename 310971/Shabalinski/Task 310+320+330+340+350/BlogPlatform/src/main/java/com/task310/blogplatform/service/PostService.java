package com.task310.blogplatform.service;

import com.task310.blogplatform.dto.PostRequestTo;
import com.task310.blogplatform.dto.PostResponseTo;
import com.task310.blogplatform.exception.EntityNotFoundException;
import com.task310.blogplatform.exception.ValidationException;
import com.task310.blogplatform.mapper.PostMapper;
import com.task310.blogplatform.model.Article;
import com.task310.blogplatform.model.Post;
import com.task310.blogplatform.model.Article;
import com.task310.blogplatform.repository.ArticleRepository;
import com.task310.blogplatform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final ArticleRepository articleRepository;
    private final PostMapper mapper;

    @Autowired
    public PostService(
            PostRepository postRepository,
            ArticleRepository articleRepository,
            PostMapper mapper) {
        this.postRepository = postRepository;
        this.articleRepository = articleRepository;
        this.mapper = mapper;
    }

    public PostResponseTo create(PostRequestTo dto) {
        validatePostRequest(dto);
        Post post = mapper.toEntity(dto);
        
        // Validate and set article
        if (dto.getArticleId() != null) {
            Article article = articleRepository.findById(dto.getArticleId())
                    .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + dto.getArticleId()));
            post.setArticle(article);
            post.setArticleId(article.getId());
        }
        
        Post saved = postRepository.save(post);
        return mapper.toResponseDto(saved);
    }

    public List<PostResponseTo> findAll() {
        return mapper.toResponseDtoList(postRepository.findAll());
    }

    public PostResponseTo findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        return postRepository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    public PostResponseTo update(Long id, PostRequestTo dto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        validatePostRequest(dto);
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        
        mapper.updateEntityFromDto(dto, existing);
        
        // Update article relationship
        if (dto.getArticleId() != null) {
            Article article = articleRepository.findById(dto.getArticleId())
                    .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + dto.getArticleId()));
            existing.setArticle(article);
            existing.setArticleId(article.getId());
        }
        
        Post updated = postRepository.save(existing);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid post id");
        }
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    public List<PostResponseTo> getPostsByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            throw new ValidationException("Invalid article id");
        }
        if (!articleRepository.existsById(articleId)) {
            throw new EntityNotFoundException("Article not found with id: " + articleId);
        }
        return postRepository.findByArticleId(articleId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validatePostRequest(PostRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("Post data is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id must not be provided in request body");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (dto.getContent().trim().length() < 2) {
            throw new ValidationException("Content must be at least 2 characters long");
        }
        if (dto.getArticleId() == null || dto.getArticleId() <= 0) {
            throw new ValidationException("Valid articleId is required");
        }
    }
}

