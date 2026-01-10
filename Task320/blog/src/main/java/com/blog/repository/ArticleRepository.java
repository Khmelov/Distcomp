package com.blog.repository;

import com.blog.entity.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends BaseJpaRepository<Article, Long> {
    List<Article> findByWriter_Id(Long writerId);
    List<Article> findByTags_Id(Long tagId);
}