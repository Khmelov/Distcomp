package com.example.demo.memoryRepository;

import com.example.demo.models.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface SaveAndSearchRepository<T extends BaseEntity> {
    T save(T entity);
    Optional<T> findById(long id);
    void deleteById(long id);
    List<T> findAll();
    boolean existById(long id);
    T update(Long id, T entity);
}
