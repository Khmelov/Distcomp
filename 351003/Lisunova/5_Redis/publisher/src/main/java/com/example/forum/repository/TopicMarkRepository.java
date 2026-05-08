package com.example.forum.repository;

import com.example.forum.entity.TopicMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicMarkRepository extends JpaRepository<TopicMark, Long> {
    List<TopicMark> findByTopicId(Long topicId);
}