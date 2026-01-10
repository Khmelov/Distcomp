package com.blog.repository;

import com.blog.entity.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends GenericRepository<Article, Long> {
    List<Article> findByWriterId(Long writerId);
    List<Article> findByTagIds(List<Long> tagIds);
    Optional<Article> findByIdWithTags(Long id);
}