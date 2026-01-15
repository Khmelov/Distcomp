package com.example.task310rest.repository.impl;

import com.example.task310rest.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Базовая InMemory реализация репозитория
 * Использует ConcurrentHashMap для thread-safe хранения данных
 * @param <T> тип сущности
 */
public abstract class InMemoryCrudRepository<T> implements CrudRepository<T, Long> {
    
    /**
     * Хранилище данных
     */
    protected final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    
    /**
     * Генератор ID
     */
    protected final AtomicLong idGenerator = new AtomicLong(0);
    
    /**
     * Получить ID сущности
     */
    protected abstract Long getId(T entity);
    
    /**
     * Установить ID сущности
     */
    protected abstract void setId(T entity, Long id);
    
    @Override
    public T save(T entity) {
        Long id = idGenerator.incrementAndGet();
        setId(entity, id);
        storage.put(id, entity);
        return entity;
    }
    
    @Override
    public T update(T entity) {
        Long id = getId(entity);
        if (id == null || !storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found for update");
        }
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
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
    
    /**
     * Очистить хранилище (полезно для тестирования)
     */
    public void clear() {
        storage.clear();
        idGenerator.set(0);
    }
}
