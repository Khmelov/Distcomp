package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.WriterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of Writer repository.
 */
@Repository
public class WriterInMemoryRepository implements WriterRepository {

    private final Map<Long, Writer> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Writer> findById(final Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Writer> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Writer save(final Writer entity) {
        Long newId = idGenerator.getAndIncrement();
        Writer toSave = entity.withId(newId);
        storage.put(newId, toSave);
        return toSave;
    }

    @Override
    public Writer update(final Writer entity) {
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
