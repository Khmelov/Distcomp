package com.example.restApi.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
