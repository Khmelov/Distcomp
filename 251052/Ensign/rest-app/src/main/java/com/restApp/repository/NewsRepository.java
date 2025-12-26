package com.restApp.repository;

import com.restApp.model.News;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepository extends AbstractInMemoryRepository<News> {
}
