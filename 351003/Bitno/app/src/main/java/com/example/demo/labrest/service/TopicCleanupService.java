package com.example.demo.labrest.service;
import com.example.demo.labrest.model.Marker;
import com.example.demo.labrest.model.Topic;
import com.example.demo.labrest.repository.MarkerRepository;
import com.example.demo.labrest.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TopicCleanupService {

    private final TopicRepository topicRepository;
    private final MarkerRepository markerRepository;

    public TopicCleanupService(TopicRepository topicRepository, MarkerRepository markerRepository) {
        this.topicRepository = topicRepository;
        this.markerRepository = markerRepository;
    }

    /**
     * Удаляет топик и все его маркеры, которые больше не используются другими топиками.
     */

}