package com.example.demo.repository;

import com.example.demo.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByName(String name);
}
