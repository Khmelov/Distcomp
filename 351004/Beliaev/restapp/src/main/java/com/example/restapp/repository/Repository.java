package com.example.discussion.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    T update(T entity);
}