package com.example.task310rest.repository.impl;

import com.example.task310rest.entity.Mark;
import com.example.task310rest.repository.MarkRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * InMemory реализация репозитория для Mark
 */
@Repository
public class MarkRepositoryImpl extends InMemoryCrudRepository<Mark> implements MarkRepository {
    
    @Override
    protected Long getId(Mark entity) {
        return entity.getId();
    }
    
    @Override
    protected void setId(Mark entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public Mark save(Mark entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return super.save(entity);
    }
    
    @Override
    public Mark update(Mark entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(entity);
    }
    
    @Override
    public Optional<Mark> findByName(String name) {
        return storage.values().stream()
                .filter(mark -> mark.getName().equals(name))
                .findFirst();
    }
    
    @Override
    public boolean existsByName(String name) {
        return storage.values().stream()
                .anyMatch(mark -> mark.getName().equals(name));
    }
}
