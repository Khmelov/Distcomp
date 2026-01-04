package com.blog.repository;

import com.blog.model.Editor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {

    Optional<Editor> findByLogin(String login);

    boolean existsByLogin(String login);

    Page<Editor> findAll(Pageable pageable);

    @Query("SELECT COUNT(e) > 0 FROM Editor e WHERE e.id = :id")
    boolean existsById(@Param("id") Long id);
}