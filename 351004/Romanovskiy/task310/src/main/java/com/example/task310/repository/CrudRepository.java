package com.example.task310.repository;

import com.example.task310.domain.entity.BaseEntity;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T extends BaseEntity> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}