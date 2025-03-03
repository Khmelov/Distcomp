package com.example.lab1.repository;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.entity.Reaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryReactionRepository implements EntityRepository<Reaction> {

    private final Map<Long, Reaction> storage = new ConcurrentHashMap<>();
    private long idCounter = 1;

    @Override
    public Reaction create(Reaction entity) {
        entity.setId(idCounter++);
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Reaction update(Reaction entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new EntityNotFoundException("Reaction with ID " + entity.getId() + " not found");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Reaction> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Reaction> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        Reaction reaction = findById(id).orElseThrow(() -> new EntityNotFoundException("Reaction with ID " + id + " not found"));
        storage.remove(reaction.getId());
    }
}
