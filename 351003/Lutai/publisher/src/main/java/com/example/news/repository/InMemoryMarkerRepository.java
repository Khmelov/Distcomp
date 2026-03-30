package com.example.news.repository;

import com.example.news.entity.Marker;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMarkerRepository implements CrudRepository<Marker, Long> {
    private final Map<Long, Marker> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Marker save(Marker entity) {
        entity.setId(idGenerator.getAndIncrement());
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Marker> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Marker> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Marker update(Marker entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}