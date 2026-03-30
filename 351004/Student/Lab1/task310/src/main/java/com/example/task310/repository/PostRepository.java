package com.example.task310.repository;

import com.example.task310.entity.Post;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository implements CrudRepository<Post, Long> {
    private final Map<Long, Post> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Post save(Post entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Post> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Post> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public void deleteById(Long id) { storage.remove(id); }
}