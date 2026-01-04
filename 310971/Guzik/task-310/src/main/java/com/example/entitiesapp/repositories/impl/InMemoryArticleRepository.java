package com.example.entitiesapp.repositories.impl;

import com.example.entitiesapp.entities.Article;
import com.example.entitiesapp.repositories.ArticleRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryArticleRepository implements ArticleRepository {
    private final Map<Long, Article> articles = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Article save(Article article) {
        if (article.getId() == null) {
            article.setId(idCounter.getAndIncrement());
            article.setCreated(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        article.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        articles.put(article.getId(), article);
        return article;
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(articles.get(id));
    }

    @Override
    public List<Article> findAll() {
        return new ArrayList<>(articles.values());
    }

    @Override
    public Article update(Article article) {
        if (!articles.containsKey(article.getId())) {
            throw new IllegalArgumentException("Article not found with id: " + article.getId());
        }
        article.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        articles.put(article.getId(), article);
        return article;
    }

    @Override
    public boolean deleteById(Long id) {
        return articles.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return articles.containsKey(id);
    }

    @Override
    public List<Article> findByWriterId(Long writerId) {
        return articles.values().stream()
                .filter(article -> article.getWriterId() != null && article.getWriterId().equals(writerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByStickerId(Long stickerId) {
        return articles.values().stream()
                .filter(article -> article.getStickers() != null &&
                        article.getStickers().stream().anyMatch(sticker -> sticker.getId().equals(stickerId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByStickerName(String stickerName) {
        return articles.values().stream()
                .filter(article -> article.getStickers() != null &&
                        article.getStickers().stream().anyMatch(sticker -> sticker.getName().equals(stickerName)))
                .collect(Collectors.toList());
    }
}