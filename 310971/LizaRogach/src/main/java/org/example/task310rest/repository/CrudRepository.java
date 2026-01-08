package org.example.task310rest.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    T save(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}


