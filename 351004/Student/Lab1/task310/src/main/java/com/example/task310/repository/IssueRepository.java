package com.example.task310.repository;

import com.example.task310.entity.Issue;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class IssueRepository implements CrudRepository<Issue, Long> {
    private final Map<Long, Issue> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Issue save(Issue entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Issue> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Issue> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public void deleteById(Long id) { storage.remove(id); }
}