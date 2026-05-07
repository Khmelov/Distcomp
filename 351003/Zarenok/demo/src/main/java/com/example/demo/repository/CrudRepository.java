package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, Long> {
    //CREATE UPDATE
    T save(T entity);

    //DELETE
    void deleteById(Long id);

    //READ
    Optional<T> findById(Long id);
    List<T> findAll();

    boolean existsById(Long id);
}
