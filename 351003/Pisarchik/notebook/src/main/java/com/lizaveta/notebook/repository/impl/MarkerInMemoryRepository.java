package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Marker;
import com.lizaveta.notebook.repository.MarkerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of Marker repository.
 */
@Repository
public class MarkerInMemoryRepository implements MarkerRepository {

    private final Map<Long, Marker> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Marker> findById(final Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Marker> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Marker save(final Marker entity) {
        Long newId = idGenerator.getAndIncrement();
        Marker toSave = entity.withId(newId);
        storage.put(newId, toSave);
        return toSave;
    }

    @Override
    public Marker update(final Marker entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(final Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(final Long id) {
        return storage.containsKey(id);
    }
}
