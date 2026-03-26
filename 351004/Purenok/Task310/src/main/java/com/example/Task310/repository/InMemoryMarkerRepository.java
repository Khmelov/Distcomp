package com.example.Task310.repository;

import com.example.Task310.bean.Marker;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMarkerRepository implements CrudRepository<Marker, Long> {

    private final Map<Long, Marker> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Marker save(Marker entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
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
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}