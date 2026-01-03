package com.task.rest.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRepository<T> implements CrudRepository<T, Long> {

    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);

    protected abstract Long getId(T entity);
    protected abstract void setId(T entity, Long id);

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            setId(entity, idGenerator.getAndIncrement());
        }
        storage.put(getId(entity), entity);
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
    public T update(T entity) {
        Long id = getId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found with id: " + id);
        }
        storage.put(id, entity);
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