package com.example.demo.repository;

import com.example.demo.models.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface StoryRepository extends BaseJpaRepository<Story> {
    @Override
    @EntityGraph(attributePaths = "tags")
    Optional<Story> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "tags")
    Page<Story> findAll(Specification<Story> spec, Pageable pageable);

    boolean existsByWriterId(Long writerId);
}
