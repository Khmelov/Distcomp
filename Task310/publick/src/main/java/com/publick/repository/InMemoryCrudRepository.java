package com.publick.repository;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryCrudRepository<T, ID> implements CrudRepository<T, ID> {

    protected final Map<ID, T> storage = new HashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);

    protected abstract ID getId(T entity);
    protected abstract void setId(T entity, ID id);

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            ID newId = generateId();
            setId(entity, newId);
        }
        storage.put(getId(entity), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public T update(T entity) {
        ID id = getId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity with id " + id + " does not exist");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public void deleteById(ID id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    @SuppressWarnings("unchecked")
    protected ID generateId() {
        return (ID) Long.valueOf(idGenerator.getAndIncrement());
    }
}