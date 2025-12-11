package com.task.rest.repository;

import com.task.rest.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T extends BaseEntity> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
