package com.lab1.lab1DC.repository;

import com.lab1.lab1DC.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepositoryJPA extends JpaRepository<Story, Long> {
}
