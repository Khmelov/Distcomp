package com.example.restapp.repository;

import com.example.restapp.model.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleRepository extends InMemoryRepository<Article> {
}