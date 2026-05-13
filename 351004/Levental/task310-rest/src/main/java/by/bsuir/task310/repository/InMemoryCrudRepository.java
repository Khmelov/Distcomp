package by.bsuir.task310.repository;

import by.bsuir.task310.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCrudRepository<T extends BaseEntity> implements CrudRepository<T> {
    protected final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public T save(T entity) {
        long id = sequence.incrementAndGet();
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
    public T update(Long id, T entity) {
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public void clear() {
        storage.clear();
        sequence.set(0);
    }
}
