package com.example.publisher.repository;

import com.example.publisher.entity.Mark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    Optional<Mark> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT m FROM Mark m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Mark> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT m FROM Mark m LEFT JOIN FETCH m.stories WHERE m.id = :id")
    Optional<Mark> findByIdWithStories(@Param("id") Long id);

    @Query("SELECT m FROM Mark m JOIN m.stories s WHERE s.id = :storyId")
    List<Mark> findByStoryId(@Param("storyId") Long storyId);

    @Query("SELECT m FROM Mark m JOIN m.stories s WHERE s.id = :storyId")
    Page<Mark> findByStoryId(@Param("storyId") Long storyId, Pageable pageable);
}