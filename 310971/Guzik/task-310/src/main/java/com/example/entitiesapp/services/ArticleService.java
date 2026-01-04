package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.ArticleRequestTo;
import com.example.entitiesapp.dto.response.ArticleResponseTo;
import com.example.entitiesapp.entities.Article;
import com.example.entitiesapp.entities.Writer;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.ArticleMapper;
import com.example.entitiesapp.repositories.ArticleRepository;
import com.example.entitiesapp.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final WriterRepository writerRepository;
    private final ArticleMapper articleMapper;

    public List<ArticleResponseTo> getAll() {
        return articleRepository.findAll().stream()
                .map(articleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return articleMapper.toResponseDto(article);
    }

    public ArticleResponseTo create(ArticleRequestTo dto) {
        validateArticleRequest(dto);

        // Проверяем существование writer
        Writer writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + dto.getWriterId()));

        Article article = articleMapper.toEntity(dto);
        article.setCreated(LocalDateTime.now());
        article.setModified(LocalDateTime.now());

        // Устанавливаем связь
        article.setWriterId(writer.getId());

        Article saved = articleRepository.save(article);
        return articleMapper.toResponseDto(saved);
    }

    public ArticleResponseTo update(Long id, ArticleRequestTo dto) {
        validateArticleRequest(dto);

        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        // Проверяем существование writer
        Writer writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + dto.getWriterId()));

        Article article = articleMapper.toEntity(dto);
        article.setId(id);
        article.setCreated(existing.getCreated());
        article.setModified(LocalDateTime.now());
        article.setPosts(existing.getPosts());
        article.setStickers(existing.getStickers());
        article.setWriterId(writer.getId());

        Article updated = articleRepository.update(article);
        return articleMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    private void validateArticleRequest(ArticleRequestTo dto) {
        if (dto.getWriterId() == null) {
            throw new ValidationException("writerId is required");
        }
        if (dto.getTitle() == null || dto.getTitle().length() < 2 || dto.getTitle().length() > 64) {
            throw new ValidationException("Title must be between 2 and 64 characters");
        }
        if (dto.getContent() == null || dto.getContent().length() < 2 || dto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
    }
}