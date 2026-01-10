package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ArticleRepositoryImpl extends InMemoryGenericRepository<Article> implements ArticleRepository {

    @Override
    public List<Article> findByWriterId(Long writerId) {
        return storage.values().stream()
                .filter(article -> writerId.equals(article.getWriterId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> findByTagIds(List<Long> tagIds) {
        // Since we removed the tags relationship, return empty list for now
        // In a real implementation, you would need a separate join table
        return List.of();
    }

    @Override
    public Optional<Article> findByIdWithTags(Long id) {
        return findById(id);
    }
}