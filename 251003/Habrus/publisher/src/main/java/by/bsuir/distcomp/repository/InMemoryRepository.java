package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.entity.Identifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryRepository<T extends Identifiable> implements IRepository<T> {
    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private static final AtomicLong counter = new AtomicLong();

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public T create(T entity) {
        Long id = counter.incrementAndGet();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
