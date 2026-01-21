package com.example.publisher.repository;

import com.example.publisher.entity.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    List<Reaction> findByStoryId(Long storyId);

    Page<Reaction> findByStoryId(Long storyId, Pageable pageable);

    boolean existsByStoryId(Long storyId);

    @Query("SELECT r FROM Reaction r WHERE r.story.id = :storyId AND LOWER(r.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    Page<Reaction> findByStoryIdAndContentContaining(
            @Param("storyId") Long storyId,
            @Param("content") String content,
            Pageable pageable);

    @Query("SELECT r FROM Reaction r LEFT JOIN FETCH r.story WHERE r.id = :id")
    Optional<Reaction> findByIdWithStory(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.story.id = :storyId")
    Long countByStoryId(@Param("storyId") Long storyId);

    void deleteByStoryId(Long storyId);
}