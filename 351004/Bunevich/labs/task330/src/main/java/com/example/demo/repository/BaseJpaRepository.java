package com.example.demo.repository;

import com.example.demo.models.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    // Все методы уже есть в JpaRepository и JpaSpecificationExecutor:
    // - save(T entity)
    // - findById(ID id)
    // - findAll()
    // - findAll(Pageable pageable) — пагинация + сортировка
    // - findAll(Specification<T> spec, Pageable pageable) — фильтрация + пагинация + сортировка
    // - deleteById(ID id)
    // - existsById(ID id)
}
