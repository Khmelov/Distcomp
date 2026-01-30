package com.sergey.orsik.repository.impl;

import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCreatorRepository implements CrudRepository<Creator> {

    private final Map<Long, Creator> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Creator save(Creator entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), new Creator(
                entity.getId(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getFirstname(),
                entity.getLastname()
        ));
        return entity;
    }

    @Override
    public Optional<Creator> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public java.util.List<Creator> findAll() {
        return storage.values().stream()
                .map(c -> new Creator(c.getId(), c.getLogin(), c.getPassword(), c.getFirstname(), c.getLastname()))
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
