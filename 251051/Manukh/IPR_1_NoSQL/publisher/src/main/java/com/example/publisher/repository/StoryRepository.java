package com.example.publisher.repository;

import com.example.publisher.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findByEditorId(Long editorId);

    Page<Story> findByEditorId(Long editorId, Pageable pageable);

    boolean existsByEditorId(Long editorId);

    @Query("SELECT s FROM Story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Story> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    @Query("SELECT s FROM Story s WHERE LOWER(s.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    Page<Story> findByContentContainingIgnoreCase(@Param("content") String content, Pageable pageable);

    @Query("SELECT s FROM Story s WHERE s.editor.id = :editorId AND LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Story> findByEditorIdAndTitleContaining(
            @Param("editorId") Long editorId,
            @Param("title") String title,
            Pageable pageable);

    @Query("SELECT s FROM Story s LEFT JOIN FETCH s.editor WHERE s.id = :id")
    Optional<Story> findByIdWithEditor(@Param("id") Long id);

    @Query("SELECT s FROM Story s " +
            "LEFT JOIN FETCH s.editor " +
            "LEFT JOIN FETCH s.marks " +
            "LEFT JOIN FETCH s.reactions " +
            "WHERE s.id = :id")
    Optional<Story> findByIdWithAllRelations(@Param("id") Long id);

    boolean existsByIdAndEditorId(Long id, Long editorId);
}