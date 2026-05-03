package com.example.task310.repository;

import com.example.task310.entity.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
}
