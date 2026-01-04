package com.task310.socialnetwork.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    T update(T entity);
    boolean deleteById(ID id);
    boolean existsById(ID id);
}