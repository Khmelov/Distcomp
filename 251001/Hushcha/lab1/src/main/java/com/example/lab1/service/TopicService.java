package com.example.lab1.service;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.dto.TopicRequestTo;
import com.example.lab1.dto.TopicResponseTo;
import com.example.lab1.entity.Topic;
import com.example.lab1.mapper.TopicMapper;
import com.example.lab1.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService implements EntityService<TopicRequestTo, TopicResponseTo> {

    private final EntityRepository<Topic> topicRepository;
    private final TopicMapper topicMapper = TopicMapper.INSTANCE;

    @Override
    public TopicResponseTo create(TopicRequestTo entityDTO) {
        Topic topic = topicMapper.toEntity(entityDTO);
        Topic savedTopic = topicRepository.create(topic);
        return topicMapper.toDTO(savedTopic);
    }

    @Override
    public TopicResponseTo getById(Long id) {
        return topicRepository.findById(id)
                .map(topicMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Topic with ID " + id + " not found"));
    }

    @Override
    public List<TopicResponseTo> getAll() {
        return topicRepository.findAll()
                .stream()
                .map(topicMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TopicResponseTo update(TopicRequestTo entityDTO) {
        Topic topic = topicMapper.toEntity(entityDTO);
        Topic updatedTopic = topicRepository.update(topic);
        return topicMapper.toDTO(updatedTopic);
    }

    @Override
    public void deleteById(Long id) {
        if (topicRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Topic with ID " + id + " not found");
        }
        topicRepository.deleteById(id);
    }
}