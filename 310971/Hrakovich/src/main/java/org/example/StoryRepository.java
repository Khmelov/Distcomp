package org.example;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {

    boolean existsByTitle(String title);

    Page<Story> findByWriterId(
            Long writerId,
            Pageable pageable
    );

    Page<Story> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );
}