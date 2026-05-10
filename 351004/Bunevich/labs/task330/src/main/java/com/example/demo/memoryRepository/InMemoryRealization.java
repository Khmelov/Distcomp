package com.example.demo.memoryRepository;

import com.example.demo.models.BaseEntity;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRealization <T extends BaseEntity> implements SaveAndSearchRepository<T>{
   protected final Map<Long, T> entityStorage = new HashMap<>();
   protected final AtomicLong idGenerator = new AtomicLong(0);
    @Override
    public T save(T entity) {
        long id = idGenerator.incrementAndGet();
        entity.setId(id);
        entityStorage.put(id, entity);
        return entity;
    }
    @Override
    public Optional<T> findById(long id) {
        return Optional.ofNullable(entityStorage.get(id));
    }
    @Override
    public void deleteById(long id) {
        entityStorage.remove(id);
    }
    @Override
    public List<T> findAll(){
        return new ArrayList<>(entityStorage.values());
    }
    @Override
    public boolean existById(long id){
        return entityStorage.containsKey(id);
    }
    @Override
    public T update(Long id, T entity){
        entity.setId(id);
        entityStorage.put(id, entity);
        return entity;
    }
}
