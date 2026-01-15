package com.example.task310rest.repository.impl;

import com.example.task310rest.entity.User;
import com.example.task310rest.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * InMemory реализация репозитория для User
 */
@Repository
public class UserRepositoryImpl extends InMemoryCrudRepository<User> implements UserRepository {
    
    @Override
    protected Long getId(User entity) {
        return entity.getId();
    }
    
    @Override
    protected void setId(User entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public User save(User entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return super.save(entity);
    }
    
    @Override
    public User update(User entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(entity);
    }
    
    @Override
    public Optional<User> findByLogin(String login) {
        return storage.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }
    
    @Override
    public boolean existsByLogin(String login) {
        return storage.values().stream()
                .anyMatch(user -> user.getLogin().equals(login));
    }
}
