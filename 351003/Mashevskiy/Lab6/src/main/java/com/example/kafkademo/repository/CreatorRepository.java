package com.example.kafkademo.repository;

import com.example.kafkademo.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByLogin(String login);
    boolean existsByLogin(String login);
}