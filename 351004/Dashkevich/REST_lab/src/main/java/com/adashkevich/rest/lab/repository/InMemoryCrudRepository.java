package com.adashkevich.rest.lab.repository;

import com.adashkevich.rest.lab.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCrudRepository<T extends BaseEntity> implements CrudRepository<T, Long> {

    private final ConcurrentSkipListMap<Long, T> storage = new ConcurrentSkipListMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public T save(T entity) {
        long id = seq.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public T update(Long id, T entity) {
        entity.setId(id);
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
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
