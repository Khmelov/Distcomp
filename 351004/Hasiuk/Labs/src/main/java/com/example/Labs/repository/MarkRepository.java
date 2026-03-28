package com.example.Labs.repository;

import com.example.Labs.entity.Mark;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarkRepository extends BaseRepository<Mark, Long> {
    Optional<Mark> findByName(String name);
}