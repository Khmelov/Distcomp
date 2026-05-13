package org.example.repository;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    Collection<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}