package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Notice;
import com.lizaveta.notebook.repository.NoticeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of Notice repository.
 */
@Repository
public class NoticeInMemoryRepository implements NoticeRepository {

    private final Map<Long, Notice> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Notice> findById(final Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Notice> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Notice save(final Notice entity) {
        Long newId = idGenerator.getAndIncrement();
        Notice toSave = entity.withId(newId);
        storage.put(newId, toSave);
        return toSave;
    }

    @Override
    public Notice update(final Notice entity) {
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

    @Override
    public List<Notice> findByStoryId(final Long storyId) {
        return storage.values().stream()
                .filter(n -> storyId.equals(n.getStoryId()))
                .toList();
    }
}
