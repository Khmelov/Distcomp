package com.example.task310.repository;

import com.example.task310.entity.Marker;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MarkerRepository implements CrudRepository<Marker, Long> {
    private final Map<Long, Marker> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Marker save(Marker entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Marker> findById(Long id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public List<Marker> findAll() { return new ArrayList<>(storage.values()); }

    @Override
    public void deleteById(Long id) { storage.remove(id); }
}