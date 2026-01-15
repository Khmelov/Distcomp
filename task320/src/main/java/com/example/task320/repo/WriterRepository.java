package com.example.task320.repo;

import com.example.task320.domain.WriterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriterRepository extends JpaRepository<WriterEntity, Long> {
    boolean existsByLogin(String login);
}
