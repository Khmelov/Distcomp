package com.example.rest.service.Impl;

import com.example.rest.dto.topic.TopicRequestTo;
import com.example.rest.dto.topic.TopicResponseTo;
import com.example.rest.dto.topic.TopicUpdate;
import com.example.rest.entity.Creator;
import com.example.rest.entity.Tag;
import com.example.rest.entity.Topic;
import com.example.rest.exceptionHandler.CreatorNotFoundException;
import com.example.rest.mapper.TopicMapper;
import com.example.rest.repository.CreatorRepository;
import com.example.rest.repository.TagRepository;
import com.example.rest.repository.TopicRepository;
import com.example.rest.service.TopicService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;
    private CreatorRepository creatorRepository;
    private TopicMapper topicMapper;
    private TagRepository tagRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, TopicMapper topicMapper, CreatorRepository creatorRepository, TagRepository tagRepository) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
        this.creatorRepository = creatorRepository;
        this.tagRepository = tagRepository;
    }
    
    @Override
    public TopicResponseTo create(TopicRequestTo topic) {
        Creator creator = creatorRepository.findById(topic.getCreatorId())
                .orElseThrow(() -> new CreatorNotFoundException(topic.getCreatorId()));

        List<Tag> tags = topic.getTags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }))
                .toList();

        Topic entity = topicMapper.toEntity(topic);
        entity.setCreator(creator);
        entity.setTags(tags);

        return topicMapper.toResponse(topicRepository.save(entity));
    }

    @Override
    public TopicResponseTo update(TopicUpdate updatedTopic) {
        Topic topic = topicRepository.findById(updatedTopic.getId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));


        if (updatedTopic.getCreatorId() != null) {
            Creator creator = creatorRepository.findById(updatedTopic.getCreatorId())
                    .orElseThrow(() -> new IllegalArgumentException("Creator to update not found"));
            topic.setCreator(creator);
        }
        if (updatedTopic.getTitle() != null) {
            topic.setTitle(updatedTopic.getTitle());
        }
        if (updatedTopic.getContent() != null) {
            topic.setContent(updatedTopic.getContent());
        }
        return topicMapper.toResponse(topicRepository.save(topic));
    }

    @Override
    public void deleteById(Long id) {
        topicRepository.deleteById(id);

    }

    @Override
    public List<TopicResponseTo> findAll() {
        return StreamSupport.stream(topicRepository.findAll().spliterator(), false)
                .map(topicMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<TopicResponseTo> findById(Long id) {
        return topicRepository.findById(id)
                .map(topicMapper::toResponse);
    }
}
