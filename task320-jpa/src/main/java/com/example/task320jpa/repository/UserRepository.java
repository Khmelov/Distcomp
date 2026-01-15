package com.example.task320jpa.repository;

import com.example.task320jpa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository для сущности User
 * Расширяет JpaRepository для базовых CRUD операций + пагинация
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Найти пользователя по логину
     */
    Optional<User> findByLogin(String login);
    
    /**
     * Проверить существование пользователя по логину
     */
    boolean existsByLogin(String login);
    
    /**
     * Найти всех пользователей с пагинацией
     */
    Page<User> findAll(Pageable pageable);
}
