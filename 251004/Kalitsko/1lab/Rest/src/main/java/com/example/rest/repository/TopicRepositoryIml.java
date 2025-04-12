package com.example.rest.repository;

import com.example.rest.entity.Topic;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TopicRepositoryIml implements TopicRepository{
    
    private final Map<Long, Topic> topics = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Topic create(Topic topic) {
        topic.setId(nextId++);
        topics.put(topic.getId(), topic);
        return topic;
    }

    @Override
    public Topic update(Topic updatedTopic) {
        if (!topics.containsKey(updatedTopic.getId())) {
            throw new IllegalArgumentException("Topic with ID " + updatedTopic.getId() + " not found");
        }
        topics.put(updatedTopic.getId(), updatedTopic);
        return updatedTopic;
    }

    @Override
    public void deleteById(Long id) {
        topics.remove(id);

    }

    @Override
    public List<Topic> findAll() {
        return topics.values().stream().toList();
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return Optional.ofNullable(topics.get(id));
    }
}
