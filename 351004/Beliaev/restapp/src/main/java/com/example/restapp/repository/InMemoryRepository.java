package com.example.restapp.repository;

import com.example.restapp.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRepository<T extends BaseEntity> implements Repository<T, Long> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong identity = new AtomicLong(0);

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(identity.incrementAndGet());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public T update(T entity) {
        if (entity.getId() != null && storage.containsKey(entity.getId())) {
            storage.put(entity.getId(), entity);
            return entity;
        }
        return null;
    }
}