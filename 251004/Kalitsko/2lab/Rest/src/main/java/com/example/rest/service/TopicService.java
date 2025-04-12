package com.example.rest.service;



import com.example.rest.dto.topic.TopicRequestTo;
import com.example.rest.dto.topic.TopicResponseTo;
import com.example.rest.dto.topic.TopicUpdate;

import java.util.List;
import java.util.Optional;

public interface TopicService {
    TopicResponseTo create(TopicRequestTo topic);

    TopicResponseTo update(TopicUpdate updatedTopic);

    void deleteById(Long id);

    List<TopicResponseTo> findAll();

    Optional<TopicResponseTo> findById(Long id);
}
