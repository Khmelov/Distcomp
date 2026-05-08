package com.example.task310.repository;

import com.example.task310.model.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    boolean existsByLogin(String login);
    Optional<Creator> findByLogin(String login);
}