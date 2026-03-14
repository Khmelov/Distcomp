package com.example.Labs.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class InMemoryRepository<T> implements CrudRepository<T, Long> {

    private final ConcurrentHashMap<Long, T> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Function<T, Long> idExtractor;
    private final IdSetter<T> idSetter;

    public interface IdSetter<T> {
        void setId(T entity, Long id);
    }

    public InMemoryRepository(Function<T, Long> idExtractor, IdSetter<T> idSetter) {
        this.idExtractor = idExtractor;
        this.idSetter = idSetter;
    }

    @Override
    public T save(T entity) {
        Long id = idGenerator.getAndIncrement();
        idSetter.setId(entity, id);
        store.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public T update(T entity) {
        Long id = idExtractor.apply(entity);
        if (id == null || !store.containsKey(id)) {
            return null;
        }
        store.put(id, entity);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}