package com.example.rest.service;

import com.example.rest.dto.TopicRequestTo;
import com.example.rest.dto.TopicResponseTo;
import com.example.rest.dto.TopicUpdate;
import com.example.rest.entity.Topic;
import com.example.rest.mapper.TopicMapper;
import com.example.rest.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;
    private TopicMapper topicMapper;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }
    
    @Override
    public TopicResponseTo create(TopicRequestTo topic) {
        return topicMapper.toResponse(topicRepository.create(topicMapper.toEntity(topic)));
    }

    @Override
    public TopicResponseTo update(TopicUpdate updatedTopic) {
        Topic topic = topicRepository.findById(updatedTopic.getId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        if (updatedTopic.getCreatorId() != null) {
            topic.setCreatorId(updatedTopic.getCreatorId());
        }
        if (updatedTopic.getTitle() != null) {
            topic.setTitle(updatedTopic.getTitle());
        }
        if (updatedTopic.getContent() != null) {
            topic.setContent(updatedTopic.getContent());
        }
        if (updatedTopic.getCreated() != null) {
            topic.setCreated(updatedTopic.getCreated());
        }
        if (updatedTopic.getModified() != null) {
            topic.setModified(updatedTopic.getModified());
        }
        if (updatedTopic.getTags() != null) {
            topic.setTags(updatedTopic.getTags());
        }

        return topicMapper.toResponse(topicRepository.update(topic));
    }

    @Override
    public void deleteById(Long id) {
        topicRepository.deleteById(id);

    }

    @Override
    public List<TopicResponseTo> findAll() {
        return topicRepository.findAll()
                .stream()
                .map(topicMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<TopicResponseTo> findById(Long id) {
        return topicRepository.findById(id)
                .map(topicMapper::toResponse);
    }
}
