// InMemoryReactionRepository.java
package com.example.repository;

import org.springframework.stereotype.Repository;
import com.example.model.Reaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryReactionRepository implements CrudRepository<Reaction, Long> {
    private final Map<Long, Reaction> reactions = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Reaction> findAll() {
        return new ArrayList<>(reactions.values());
    }

    @Override
    public Optional<Reaction> findById(Long id) {
        return Optional.ofNullable(reactions.get(id));
    }

    @Override
    public Reaction save(Reaction reaction) {
        if (reaction.getId() == null) {
            reaction.setId(idCounter.getAndIncrement());
        }
        reactions.put(reaction.getId(), reaction);
        return reaction;
    }

    @Override
    public Reaction update(Reaction reaction) {
        if (reaction.getId() == null || !reactions.containsKey(reaction.getId())) {
            throw new IllegalArgumentException("Reaction not found with id: " + reaction.getId());
        }
        reaction.setModified(java.time.LocalDateTime.now());
        reactions.put(reaction.getId(), reaction);
        return reaction;
    }

    @Override
    public boolean deleteById(Long id) {
        return reactions.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return reactions.containsKey(id);
    }

    public List<Reaction> findByStoryId(Long storyId) {
        return reactions.values().stream()
                .filter(reaction -> reaction.getStoryId().equals(storyId))
                .collect(Collectors.toList());
    }

    public boolean existsByStoryId(Long storyId) {
        return reactions.values().stream()
                .anyMatch(reaction -> reaction.getStoryId().equals(storyId));
    }
}