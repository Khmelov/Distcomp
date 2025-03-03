package com.example.lab1.repository;

import java.util.List;
import java.util.Optional;

public interface EntityRepository<E> {

    E create(E entity);

    E update(E entity);

    Optional<E> findById(Long id);

    List<E> findAll();

    void deleteById(Long id);

}
