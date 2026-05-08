package com.example.forum.repository;

import com.example.forum.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Page<Topic> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    boolean existsByTitle(String title);
}
