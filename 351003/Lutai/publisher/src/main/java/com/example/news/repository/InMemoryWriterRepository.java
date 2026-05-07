package com.example.news.repository;

import com.example.news.entity.Writer;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryWriterRepository implements CrudRepository<Writer, Long> {
    private final Map<Long, Writer> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Writer save(Writer entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Writer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Writer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Writer update(Writer entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}