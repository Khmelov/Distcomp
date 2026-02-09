package com.example.lab1.repository;

import java.util.Optional;

public interface CrudRepository<T> {
    Iterable<T> getAllEntities();

    Optional<T> getEntityById(Long id);

    T createEntity(T entity);

    void deleteEntity(Long id);

    boolean existsEntity(Long id);
}
