package com.example.storyapp.repository;

import com.example.storyapp.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    boolean existsByUserIdAndTitle(Long userId, String title);
}