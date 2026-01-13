package com.publick.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CrudRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // All basic CRUD operations are inherited from JpaRepository
    // All specification operations are inherited from JpaSpecificationExecutor

    // Additional convenience methods
    default T update(T entity) {
        return save(entity);
    }
}