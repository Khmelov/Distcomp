package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WriterRepository extends JpaRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
    boolean existsByLogin(String login);
}