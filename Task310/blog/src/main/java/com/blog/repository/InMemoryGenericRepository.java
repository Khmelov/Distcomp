package com.blog.repository;

import com.blog.entity.BaseEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryGenericRepository<T extends BaseEntity> implements GenericRepository<T, Long> {

    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            // Create new entity
            entity.setId(idGenerator.getAndIncrement());
            entity.setCreated(LocalDateTime.now());
        } else {
            // Update existing entity
            T existing = storage.get(entity.getId());
            if (existing != null) {
                entity.setCreated(existing.getCreated());
            }
        }
        entity.setModified(LocalDateTime.now());
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