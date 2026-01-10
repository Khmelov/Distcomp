package com.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    List<T> findAll(Specification<T> spec);
    Page<T> findAll(Specification<T> spec, Pageable pageable);
    Optional<T> findById(ID id);
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
    long count(Specification<T> spec);
}