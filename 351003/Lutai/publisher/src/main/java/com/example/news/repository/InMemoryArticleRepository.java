package com.example.news.repository;

import com.example.news.entity.Article;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryArticleRepository implements CrudRepository<Article, Long> {
    private final Map<Long, Article> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Article save(Article entity) {
        entity.setId(idGenerator.getAndIncrement());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Article> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Article update(Article entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}