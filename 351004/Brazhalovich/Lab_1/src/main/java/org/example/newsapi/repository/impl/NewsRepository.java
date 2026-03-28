package org.example.newsapi.repository.impl;

import org.example.newsapi.entity.News;
import org.example.newsapi.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepository extends InMemoryRepository<News> {
}