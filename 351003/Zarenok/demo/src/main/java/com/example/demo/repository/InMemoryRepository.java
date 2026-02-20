package com.example.demo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class InMemoryRepository<T, Long> implements CrudRepository<T, Long>{

    protected final HashMap<Long, T> storage = new HashMap<>();
    //long напрямую соответствует BIGINT в СУБД
    protected long id = 0;

    protected abstract Long generatedId();
    protected abstract Long getId(T entity);
    protected abstract void setId(T entity, Long id);

    @Override
    public T save(T entity) {
        Long id = getId(entity);
        if (id == null){
            id = generatedId();
            setId(entity, id);
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
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
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
