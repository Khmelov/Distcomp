package com.example.restapp.service;

import com.example.restapp.dto.request.ArticleRequestTo;
import com.example.restapp.dto.response.ArticleResponseTo;
import com.example.restapp.exception.EntityNotFoundException;
import com.example.restapp.mapper.ArticleMapper;
import com.example.restapp.model.Article;
import com.example.restapp.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository repository;
    private final ArticleMapper mapper;

    public ArticleResponseTo create(ArticleRequestTo request) {
        Article article = mapper.toEntity(request);
        article.setCreated(LocalDateTime.now());
        article.setModified(LocalDateTime.now());
        Article saved = repository.save(article);
        return mapper.toResponse(saved);
    }

    public List<ArticleResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article article = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));

        mapper.updateEntityFromDto(request, article);
        article.setModified(LocalDateTime.now());
        repository.update(article);
        return mapper.toResponse(article);
    }

    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Article not found with id: " + id);
        }
    }
}