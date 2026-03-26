package com.example.task310.repository;

import com.example.task310.entity.Writer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class WriterRepository implements CrudRepository<Writer, Long> {
    private final Map<Long, Writer> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        save(new Writer(null, "fcvlad2005@gmail.com", "password123", "Владислав", "Студент"));
    }

    @Override
    public Writer save(Writer entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Writer> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Writer> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public void deleteById(Long id) { storage.remove(id); }
}