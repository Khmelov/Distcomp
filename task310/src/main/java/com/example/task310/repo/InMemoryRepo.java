package com.example.task310.repo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryRepo<E> {

    protected final Map<Long, E> storage = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(0);

    protected abstract E withId(E entity, long id);

    public E create(E entity) {
        long id = idGenerator.incrementAndGet();
        E created = withId(entity, id);
        storage.put(id, created);
        return created;
    }

    public Optional<E> find(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<E> findAll() {
        return new ArrayList<>(storage.values());
    }

    public E update(long id, E entity) {
        E updated = withId(entity, id);
        storage.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        storage.remove(id);
    }

    public boolean exists(long id) {
        return storage.containsKey(id);
    }
}
