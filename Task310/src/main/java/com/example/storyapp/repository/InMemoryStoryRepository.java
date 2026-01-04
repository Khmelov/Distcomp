package com.example.storyapp.repository;

import com.example.storyapp.model.Story;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryStoryRepository implements CrudRepository<Story, Long> {
    private final Map<Long, Story> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Story> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Story> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Story save(Story story) {
        if (story.getId() == null) {
            story.setId(idGenerator.getAndIncrement());
        }
        story.setModified(java.time.Instant.now());
        store.put(story.getId(), story);
        return story;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public List<Story> findByUserId(Long userId) {
        return store.values().stream()
                .filter(story -> userId.equals(story.getUserId()))
                .collect(Collectors.toList());
    }
}