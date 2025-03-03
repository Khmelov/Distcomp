package com.example.lab1.repository;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements EntityRepository<User> {

    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private long idCounter = 1;

    @Override
    public User create(User entity) {
        entity.setId(idCounter++);
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public User update(User entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new EntityNotFoundException("User with ID " + entity.getId() + " not found");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        User user = findById(id).orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        storage.remove(user.getId());
    }
}
