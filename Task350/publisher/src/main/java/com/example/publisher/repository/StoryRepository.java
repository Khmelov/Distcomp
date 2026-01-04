package com.example.publisher.repository;

import com.example.publisher.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    boolean existsByUserIdAndTitle(Long userId, String title);
}