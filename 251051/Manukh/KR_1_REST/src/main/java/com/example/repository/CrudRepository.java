// CrudRepository.java
package com.example.repository;

import com.example.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T extends BaseEntity, K> {
    List<T> findAll();
    Optional<T> findById(K id);
    T save(T entity);
    T update(T entity);
    boolean deleteById(K id);
    boolean existsById(K id);
}