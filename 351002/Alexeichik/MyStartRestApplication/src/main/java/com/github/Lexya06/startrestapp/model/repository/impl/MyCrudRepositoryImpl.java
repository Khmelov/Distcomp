package com.github.Lexya06.startrestapp.model.repository.impl;


import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;

import java.util.Optional;
import java.util.Set;

public interface MyCrudRepositoryImpl<T extends BaseEntity> {
    Optional<T> findById(Long id);
    T save(T t);
    void deleteById(Long id);
    Set<T> findAll();
}
