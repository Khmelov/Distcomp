// InMemoryStoryRepository.java
package com.example.repository;

import org.springframework.stereotype.Repository;
import com.example.model.Story;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryStoryRepository implements CrudRepository<Story, Long> {
    private final Map<Long, Story> stories = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Story> findAll() {
        return new ArrayList<>(stories.values());
    }

    @Override
    public Optional<Story> findById(Long id) {
        return Optional.ofNullable(stories.get(id));
    }

    @Override
    public Story save(Story story) {
        if (story.getId() == null) {
            story.setId(idCounter.getAndIncrement());
        }
        stories.put(story.getId(), story);
        return story;
    }

    @Override
    public Story update(Story story) {
        if (story.getId() == null || !stories.containsKey(story.getId())) {
            throw new IllegalArgumentException("Story not found with id: " + story.getId());
        }
        story.setModified(java.time.LocalDateTime.now());
        stories.put(story.getId(), story);
        return story;
    }

    @Override
    public boolean deleteById(Long id) {
        return stories.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return stories.containsKey(id);
    }

    public List<Story> findByEditorId(Long editorId) {
        return stories.values().stream()
                .filter(story -> story.getEditorId().equals(editorId))
                .collect(Collectors.toList());
    }

    public boolean existsByEditorId(Long editorId) {
        return stories.values().stream()
                .anyMatch(story -> story.getEditorId().equals(editorId));
    }

    public List<Story> findByMarkIdsContaining(Long markId) {
        return stories.values().stream()
                .filter(story -> story.getMarkIds().contains(markId))
                .collect(Collectors.toList());
    }
}