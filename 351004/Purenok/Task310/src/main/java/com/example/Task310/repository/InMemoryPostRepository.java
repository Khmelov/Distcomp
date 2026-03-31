package com.example.Task310.repository;

import com.example.Task310.bean.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPostRepository implements CrudRepository<Post, Long> {

    private final Map<Long, Post> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Post save(Post entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Post update(Post entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}