package com.example.lab1.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.lab1.model.Post;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final Map<Long, Post> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public List<Post> getAllEntities() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Post> getEntityById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Post createEntity(Post entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            entity.setId(counter.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteEntity(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsEntity(Long id) {
        return storage.containsKey(id);
    }
}
