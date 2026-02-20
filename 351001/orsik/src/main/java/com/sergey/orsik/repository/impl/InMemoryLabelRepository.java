package com.sergey.orsik.repository.impl;

import com.sergey.orsik.entity.Label;
import com.sergey.orsik.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLabelRepository implements CrudRepository<Label> {

    private final Map<Long, Label> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Label save(Label entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), new Label(entity.getId(), entity.getName()));
        return entity;
    }

    @Override
    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Label> findAll() {
        return storage.values().stream()
                .map(l -> new Label(l.getId(), l.getName()))
                .toList();
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
