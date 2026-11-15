package com.example.task330.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

// Общий интерфейс для хранения, поиска и фильтрации сущностей.
@NoRepositoryBean
public interface BaseRepository<T, ID>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
