package com.labs.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Базовый интерфейс репозитория с поддержкой CRUD операций,
 * пагинации, фильтрации и сортировки.
 *
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> 
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    // Методы findAll(Pageable) и findAll(Specification, Pageable) 
    // уже предоставляются JpaRepository и JpaSpecificationExecutor
}

