package com.example.discussion.repository;

import com.example.discussion.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByName(String name);
}
