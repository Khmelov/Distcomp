package com.example.restApi.services;

import com.example.restApi.dto.request.ArticleRequestTo;
import com.example.restApi.dto.response.ArticleResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.ArticleMapper;
import com.example.restApi.model.Article;
import com.example.restApi.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    public ArticleService(ArticleRepository articleRepository, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    public List<ArticleResponseTo> getAll() {
        return articleRepository.findAll().stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + id));
        return articleMapper.toDto(article);
    }

    public ArticleResponseTo create(ArticleRequestTo request) {
        Article article = articleMapper.toEntity(request);
        Article saved = articleRepository.save(article);
        return articleMapper.toDto(saved);
    }

    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + id));
        articleMapper.updateEntity(article, request);
        article.setModified(LocalDateTime.now());
        Article updated = articleRepository.save(article);
        return articleMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new NotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    public List<ArticleResponseTo> getByUserId(Long userId) {
        return articleRepository.findByUserId(userId).stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ArticleResponseTo> getByMarkerId(Long markerId) {
        return articleRepository.findByMarkerId(markerId).stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }
}
