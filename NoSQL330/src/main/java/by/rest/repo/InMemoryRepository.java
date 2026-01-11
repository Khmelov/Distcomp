// src/main/java/by/rest/repo/InMemoryRepository.java
package by.rest.repo;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryRepository<T> implements CrudRepository<T, Long> {
    private final Map<Long, T> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);
    private final Method getId;
    private final Method setId;

    public InMemoryRepository(Class<T> type) {
        try {
            getId = type.getMethod("getId");
            setId = type.getMethod("setId", Long.class);
        } catch (Exception e) {
            throw new IllegalStateException("Entity must have getId()/setId(Long)", e);
        }
    }

    @Override
    public T save(T entity) {
        try {
            Long id = (Long) getId.invoke(entity);
            if (id == null) {
                id = seq.getAndIncrement();
                setId.invoke(entity, id);
            }
            store.put(id, entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Optional<T> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<T> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public T update(Long id, T entity) {
        if (!store.containsKey(id)) throw new NoSuchElementException("Not found id=" + id);
        try {
            setId.invoke(entity, id);
            store.put(id, entity);
            return entity;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override public void deleteById(Long id) { store.remove(id); }
}
