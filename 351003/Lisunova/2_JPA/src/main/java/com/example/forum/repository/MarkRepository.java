package com.example.forum.repository;

import com.example.forum.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    boolean existsByName(String name);
}
