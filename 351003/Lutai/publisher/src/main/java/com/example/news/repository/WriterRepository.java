package com.example.news.repository;

import com.example.news.entity.Writer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {
    Page<Writer> findAll(Pageable pageable);

    boolean existsByLogin(String login);
}