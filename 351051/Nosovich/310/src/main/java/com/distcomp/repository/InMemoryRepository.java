package com.distcomp.repository;

import com.distcomp.model.AbstractEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory implementation of {@link CrudRepository}.
 *
 * <p>Subclasses only narrow the entity type, so the storage algorithm is
 * written exactly once.</p>
 *
 * @param <T> entity type handled by this repository
 */
public abstract class InMemoryRepository<T extends AbstractEntity> implements CrudRepository<T> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong();

    @Override
    public T save(T entity) {
        entity.setId(sequence.incrementAndGet());
        return update(entity);
    }

    @Override
    public T update(T entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity identifier must be assigned before it can be stored");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(AbstractEntity::getId))
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public Optional<T> deleteById(Long id) {
        return id == null ? Optional.empty() : Optional.ofNullable(storage.remove(id));
    }

    /** Removes every stored entity, used by tests and by the "reset" flow. */
    protected void deleteAll() {
        storage.clear();
    }
}
