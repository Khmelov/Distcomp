package by.bsuir.task310.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractInMemoryStorage<T> implements CrudStorage<T> {
    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    protected abstract Long getId(T entity);

    protected abstract void setId(T entity, Long id);

    @Override
    public List<T> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparingLong(this::getId))
                .toList();
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public T save(T entity) {
        Long id = sequence.incrementAndGet();
        setId(entity, id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        storage.put(getId(entity), entity);
        return entity;
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
