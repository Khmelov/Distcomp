package com.example.rv.api.repository;

import java.util.Optional;

public interface CrudRepository <T, ID>{
    Iterable<T> getAll();
    Optional<T> getById(ID id);
    Optional<T> save(T entity);
    Optional<T> update(T entity);
    boolean delete(T entity);

}
