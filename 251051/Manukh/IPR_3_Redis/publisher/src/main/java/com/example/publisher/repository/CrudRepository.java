// CrudRepository.java
package com.example.publisher.repository;

import com.example.publisher.model.BaseEntity;

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