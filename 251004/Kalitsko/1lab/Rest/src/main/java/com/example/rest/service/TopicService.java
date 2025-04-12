package com.example.rest.service;



import com.example.rest.dto.TopicRequestTo;
import com.example.rest.dto.TopicResponseTo;
import com.example.rest.dto.TopicUpdate;

import java.util.List;
import java.util.Optional;

public interface TopicService {
    TopicResponseTo create(TopicRequestTo topic);

    TopicResponseTo update(TopicUpdate updatedTopic);

    void deleteById(Long id);

    List<TopicResponseTo> findAll();

    Optional<TopicResponseTo> findById(Long id);
}
