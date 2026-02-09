package com.lizaveta.notebook.repository.impl;

import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.repository.StoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of Story repository.
 * Maintains Story-Marker many-to-many relationship via story.markerIds.
 */
@Repository
public class StoryInMemoryRepository implements StoryRepository {

    private final Map<Long, Story> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Story> findById(final Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Story> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public Story save(final Story entity) {
        Long newId = idGenerator.getAndIncrement();
        Story toSave = entity.withId(newId);
        storage.put(newId, toSave);
        return toSave;
    }

    @Override
    public Story update(final Story entity) {
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
    public List<Story> findByMarkerId(final Long markerId) {
        return storage.values().stream()
                .filter(s -> s.getMarkerIds().contains(markerId))
                .toList();
    }
}
