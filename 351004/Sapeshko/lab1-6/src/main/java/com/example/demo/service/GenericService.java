package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

public interface GenericService<T, ID> {
    T create(T entity);
    T update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    Page<T> findAll(Pageable pageable);
    Page<T> findAll(Specification<T> spec, Pageable pageable);
}