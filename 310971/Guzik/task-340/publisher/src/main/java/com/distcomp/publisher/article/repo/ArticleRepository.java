package com.distcomp.publisher.article.repo;

import com.distcomp.publisher.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
