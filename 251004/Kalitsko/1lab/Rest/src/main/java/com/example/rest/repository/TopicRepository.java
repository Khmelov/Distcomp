package com.example.rest.repository;

import com.example.rest.entity.Topic;

import java.util.List;
import java.util.Optional;


public interface TopicRepository {
    Topic create(Topic topic);
    Topic update(Topic updatedTopic);
    void deleteById(Long id);
    List<Topic> findAll();
    Optional<Topic> findById(Long id);
}
