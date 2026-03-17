package com.lizaveta.notebook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    T save(T entity);

    T update(T entity);

    boolean deleteById(ID id);

    boolean existsById(ID id);
}
