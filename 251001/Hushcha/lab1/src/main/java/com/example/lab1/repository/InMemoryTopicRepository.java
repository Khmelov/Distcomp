package com.example.lab1.repository;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.entity.Topic;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTopicRepository implements EntityRepository<Topic> {

    private final Map<Long, Topic> storage = new ConcurrentHashMap<>();
    private long idCounter = 1;

    @Override
    public Topic create(Topic entity) {
        entity.setId(idCounter++);
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Topic update(Topic entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new EntityNotFoundException("Topic with ID " + entity.getId() + " not found");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Topic> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        Topic topic = findById(id).orElseThrow(() -> new EntityNotFoundException("Topic with ID " + id + " not found"));
        storage.remove(topic.getId());
    }
}