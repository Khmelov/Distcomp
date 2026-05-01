package com.example.task310.repository;

import com.example.task310.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT m FROM Mark m WHERE m.name LIKE 'red%' OR m.name LIKE 'green%' OR m.name LIKE 'blue%'")
    List<Mark> findAllTestMarks();

    @Modifying
    @Transactional
    @Query("DELETE FROM Mark m WHERE m.name = ?1")
    int deleteByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Mark m WHERE m.name LIKE 'red%' OR m.name LIKE 'green%' OR m.name LIKE 'blue%'")
    int deleteAllTestMarks();
}