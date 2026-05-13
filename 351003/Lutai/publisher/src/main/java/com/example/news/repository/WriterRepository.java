package com.example.news.repository;

import com.example.news.entity.Writer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {
    Page<Writer> findAll(Pageable pageable);
    Optional<Writer> findByLogin(String login);
    boolean existsByLogin(String login);
}