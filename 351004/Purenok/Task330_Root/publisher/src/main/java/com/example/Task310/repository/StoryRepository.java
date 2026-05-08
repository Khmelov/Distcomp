package com.example.Task310.repository;
import com.example.Task310.bean.Story;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StoryRepository extends JpaRepository<Story, Long> {
    boolean existsByTitle(String title);
}

