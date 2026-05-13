package org.example.repository;

import org.example.model.News;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepository extends InMemoryRepository<News> {}