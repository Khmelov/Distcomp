package com.example.task361.repository;

import com.example.task361.domain.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByLogin(String login);
    boolean existsByLogin(String login);
}
