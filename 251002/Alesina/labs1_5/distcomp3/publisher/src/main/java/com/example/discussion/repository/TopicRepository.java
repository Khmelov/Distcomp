package com.example.discussion.repository;

import com.example.discussion.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitle(String title);
}