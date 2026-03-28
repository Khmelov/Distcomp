package com.example.forum.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCrudRepository<T> implements CrudRepository<T, Long> {

    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    protected final AtomicLong sequence = new AtomicLong(0L);

    protected final IdAccessor<T> idAccessor;

    public InMemoryCrudRepository(IdAccessor<T> idAccessor) {
        this.idAccessor = idAccessor;
    }

    @Override
    public T save(T entity) {
        Long id = sequence.incrementAndGet();
        idAccessor.setId(entity, id);
        storage.put(id, entity);
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
        Long id = idAccessor.getId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new NoSuchElementException("Entity not found");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    public interface IdAccessor<T> {
        Long getId(T entity);
        void setId(T entity, Long id);
    }

    public void clear() {
        storage.clear();
        sequence.set(0);
    }

}
