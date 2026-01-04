package com.example.entitiesapp.services;

import com.example.entitiesapp.dto.request.ArticleRequestTo;
import com.example.entitiesapp.dto.response.ArticleResponseTo;
import com.example.entitiesapp.entities.Article;
import com.example.entitiesapp.entities.Writer;
import com.example.entitiesapp.exceptions.DuplicateResourceException;
import com.example.entitiesapp.exceptions.ResourceNotFoundException;
import com.example.entitiesapp.exceptions.ValidationException;
import com.example.entitiesapp.mappers.ArticleMapper;
import com.example.entitiesapp.repositories.ArticleRepository;
import com.example.entitiesapp.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    @Transactional
    public ArticleResponseTo create(ArticleRequestTo dto) {
        validateArticleRequest(dto);

        Writer writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + dto.getWriterId()));

        if (articleRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Article with title '" + dto.getTitle() + "' already exists");
        }

        Article article = articleMapper.toEntity(dto);
        article.setWriter(writer);

        Article saved = articleRepository.save(article);
        return articleMapper.toResponseDto(saved);
    }

    @Transactional
    public ArticleResponseTo update(Long id, ArticleRequestTo dto) {
        validateArticleRequest(dto);

        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        Writer writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found with id: " + dto.getWriterId()));

        if (!existing.getTitle().equals(dto.getTitle()) &&
                articleRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Article with title '" + dto.getTitle() + "' already exists");
        }

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setWriter(writer);

        Article updated = articleRepository.save(existing);
        return articleMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    public List<ArticleResponseTo> findByWriterId(Long writerId) {
        return articleRepository.findByWriterId(writerId).stream()
                .map(articleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseTo> findByStickerId(Long stickerId) {
        return articleRepository.findByStickerId(stickerId).stream()
                .map(articleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseTo> findByStickerName(String stickerName) {
        return articleRepository.findByStickerName(stickerName).stream()
                .map(articleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validateArticleRequest(ArticleRequestTo dto) {
        if (dto.getWriterId() == null) {
            throw new ValidationException("writerId is required");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (dto.getTitle().length() < 2 || dto.getTitle().length() > 64) {
            throw new ValidationException("Title must be between 2 and 64 characters");
        }
        if (dto.getContent() == null) {
            throw new ValidationException("Content is required");
        }

        // Проверяем, что content - строка
        if (!(dto.getContent() instanceof String)) {
            throw new ValidationException("Content must be a string");
        }

        String contentStr = (String) dto.getContent();
        if (contentStr.trim().isEmpty()) {
            throw new ValidationException("Content cannot be empty");
        }
        if (contentStr.length() < 2 || contentStr.length() > 2048) {
            throw new ValidationException("Content must be between 2 and 2048 characters");
        }
    }
}