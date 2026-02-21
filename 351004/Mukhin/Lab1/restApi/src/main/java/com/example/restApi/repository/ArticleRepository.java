package com.example.restApi.repository;

import com.example.restApi.model.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticleRepository extends InMemoryRepository<Article> {

    public List<Article> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(article -> article.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Article> findByMarkerId(Long markerId) {
        return storage.values().stream()
                .filter(article -> article.getMarkerIds().contains(markerId))
                .collect(Collectors.toList());
    }
}
