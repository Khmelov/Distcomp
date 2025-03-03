package com.example.lab1.repository;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTagRepository implements EntityRepository<Tag>{

    private final Map<Long, Tag> storage = new ConcurrentHashMap<>();
    private long idCounter = 1;

    @Override
    public Tag create(Tag entity) {
        entity.setId(idCounter++);
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new EntityNotFoundException("Tag with ID " + entity.getId() + " not found");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        Tag tag = findById(id).orElseThrow(() -> new EntityNotFoundException("Tag with ID " + id + " not found"));
        storage.remove(tag.getId());
    }
}
