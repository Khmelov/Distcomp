package com.example.discussion.repository;


import com.example.discussion.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

//
//@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String title);
}
