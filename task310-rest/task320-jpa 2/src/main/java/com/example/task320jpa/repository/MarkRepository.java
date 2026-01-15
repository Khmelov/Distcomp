package com.example.task320jpa.repository;

import com.example.task320jpa.entity.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository для сущности Mark
 */
@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    
    /**
     * Найти метку по имени
     */
    Optional<Mark> findByName(String name);
    
    /**
     * Проверить существование метки по имени
     */
    boolean existsByName(String name);
    
    /**
     * Найти все метки с пагинацией
     */
    Page<Mark> findAll(Pageable pageable);
}
