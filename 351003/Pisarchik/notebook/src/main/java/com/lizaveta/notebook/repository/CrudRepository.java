package com.lizaveta.notebook.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for CRUD operations on entities.
 *
 * @param <T>  entity type
 * @param <ID> identifier type
 */
public interface CrudRepository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    boolean deleteById(ID id);

    boolean existsById(ID id);
}
