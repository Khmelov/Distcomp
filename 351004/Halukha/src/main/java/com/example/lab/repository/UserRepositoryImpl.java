package com.example.lab.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.lab.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public List<User> getAllEntities() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<User> getEntityById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public User createEntity(User user) {
        if (user.getId() == null || !storage.containsKey(user.getId())) {
            user.setId(counter.getAndIncrement());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteEntity(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsEntity(Long id) {
        return storage.containsKey(id);
    }
}