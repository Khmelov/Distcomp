package com.blog.repository;

import com.blog.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Page<Topic> findAll(Pageable pageable);

    List<Topic> findByEditorId(Long editorId);

    boolean existsByTitle(String title);

    // Если нужно найти по заголовку (опционально):
    List<Topic> findByTitle(String title);

    @Query("SELECT t FROM Topic t WHERE t.editor.id = :editorId")
    Page<Topic> findByEditorId(@Param("editorId") Long editorId, Pageable pageable);

    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Topic> findByTagId(@Param("tagId") Long tagId);

    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.id = :tagId")
    Page<Topic> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.id = :id")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.editor.id = :editorId")
    boolean existsByEditorId(@Param("editorId") Long editorId);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.id = :id AND t.editor.id = :editorId")
    boolean existsByIdAndEditorId(@Param("id") Long id, @Param("editorId") Long editorId);
}

