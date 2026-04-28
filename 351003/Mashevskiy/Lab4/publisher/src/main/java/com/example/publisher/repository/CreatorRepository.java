package com.example.publisher.repository;

import com.example.publisher.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByLogin(String login);
}