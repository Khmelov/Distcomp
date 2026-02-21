package com.example.forum.service.impl;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.response.TopicResponseTo;
import com.example.forum.entity.Topic;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.TopicMapper;
import com.example.forum.repository.TopicRepository;
import com.example.forum.service.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository repository;
    private final TopicMapper mapper;

    public TopicServiceImpl(TopicRepository repository, TopicMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TopicResponseTo create(TopicRequestTo request) {
        validate(request);

        Topic topic = mapper.toEntity(request);
        topic.setCreated(OffsetDateTime.now());
        topic.setModified(OffsetDateTime.now());

        Topic saved = repository.save(topic);
        return mapper.toResponse(saved);
    }

    @Override
    public TopicResponseTo getById(Long id) {
        Topic topic = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic not found", "40411"));
        return mapper.toResponse(topic);
    }

    @Override
    public List<TopicResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TopicResponseTo update(Long id, TopicRequestTo request) {
        validate(request);

        Topic existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic not found", "40412"));

        existing.setUserId(request.getUserId());
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setModified(OffsetDateTime.now());

        Topic updated = repository.update(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Topic not found", "40413");
        }
        repository.deleteById(id);
    }


    private void validate(TopicRequestTo request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("UserId is required", "40011");
        }
        if (!StringUtils.hasText(request.getTitle()) ||
                request.getTitle().length() < 2 ||
                request.getTitle().length() > 64) {
            throw new BadRequestException("Invalid title", "40012");
        }
        if (!StringUtils.hasText(request.getContent()) ||
                request.getContent().length() < 4 ||
                request.getContent().length() > 2048) {
            throw new BadRequestException("Invalid content", "40013");
        }
    }
}
