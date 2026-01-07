package com.task310.blogplatform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CrudRepository<T, ID> extends JpaRepository<T, ID> {
    // Basic CRUD operations are inherited from JpaRepository
    // Additional methods can be added here if needed
}
