package com.blog.repository;

import com.blog.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    Page<Tag> findAll(Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Tag t WHERE t.id = :id")
    boolean existsById(@Param("id") Long id);
}

