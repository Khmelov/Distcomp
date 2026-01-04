package com.blog.repository.impl;

import com.blog.model.Topic;
import com.blog.repository.TopicRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTopicRepository implements TopicRepository {
    private final Map<Long, Topic> topics = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Topic> findAll() {
        return new ArrayList<>(topics.values());
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return Optional.ofNullable(topics.get(id));
    }

    @Override
    public Topic save(Topic topic) {
        if (topic.getId() == null) {
            topic.setId(idCounter.getAndIncrement());
            topic.setCreated(LocalDateTime.now());
            topic.setModified(LocalDateTime.now());
        }
        topics.put(topic.getId(), topic);
        return topic;
    }

    @Override
    public Topic update(Topic topic) {
        if (topic.getId() == null || !topics.containsKey(topic.getId())) {
            throw new IllegalArgumentException("Topic not found");
        }
        Topic existing = topics.get(topic.getId());
        topic.setCreated(existing.getCreated());
        topic.setModified(LocalDateTime.now());
        topics.put(topic.getId(), topic);
        return topic;
    }

    @Override
    public boolean deleteById(Long id) {
        return topics.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return topics.containsKey(id);
    }

    @Override
    public List<Topic> findByEditorId(Long editorId) {
        return topics.values().stream()
                .filter(topic -> topic.getEditorId().equals(editorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Topic> findByTagIdsContaining(Long tagId) {
        return topics.values().stream()
                .filter(topic -> topic.getTagIds() != null && topic.getTagIds().contains(tagId))
                .collect(Collectors.toList());
    }
}