package com.example.task320.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

// Общий интерфейс для поиска сущностей с поддержкой фильтрации, пагинации и сортировки.
public interface BaseService<T, ID, R> {

    // Получение всех записей с учётом пагинации.
    Page<R> findAll(Pageable pageable);

    // Получение всех записей с учётом сортировки.
    List<R> findAll(Sort sort);

    // Поиск записей по условиям фильтрации с пагинацией.
    Page<R> findAll(Specification<T> specification, Pageable pageable);

    // Поиск записи по идентификатору.
    Optional<R> findById(ID id);
}