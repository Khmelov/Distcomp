package com.example.forum.service.impl;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.response.TopicResponseTo;
import com.example.forum.entity.Topic;
import com.example.forum.entity.User;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.ForbiddenException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.TopicMapper;
import com.example.forum.repository.TopicRepository;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository repository;
    private final TopicMapper mapper;
    private final UserRepository userRepository;

    public TopicServiceImpl(TopicRepository repository, TopicMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TopicResponseTo create(TopicRequestTo request) {
        // 1. Проверка на дубликаты в самом начале
        if (repository.existsByTitle(request.getTitle())) {
            throw new ForbiddenException("Topic with this title already exists");
        }

        validate(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found", "40314"));

        Topic topic = new Topic();
        topic.setUser(user);
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setCreated(OffsetDateTime.now());
        topic.setModified(OffsetDateTime.now());

        Topic saved = repository.save(topic);
        return mapToResponse(saved);
    }

    // Вспомогательный метод для правильного маппинга меток [cite: 1592, 2571]
    private TopicResponseTo mapToResponse(Topic topic) {
        TopicResponseTo response = mapper.toResponse(topic);
        if (topic.getTopicMarks() != null && !topic.getTopicMarks().isEmpty()) {
            response.setMarkIds(topic.getTopicMarks().stream()
                    .map(tm -> tm.getMark().getId())
                    .collect(Collectors.toSet())); // Используем toSet()
        } else {
            response.setMarkIds(java.util.Collections.emptySet()); // Используем emptySet()
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponseTo getById(Long id) {
        Topic topic = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic not found", "40411"));
        return mapToResponse(topic); // Используй общий метод, чтобы везде была одинаковая логика
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicResponseTo> getAll(String titleFilter, Pageable pageable) {
        Page<Topic> page = (titleFilter == null || titleFilter.isBlank())
                ? repository.findAll(pageable)
                : repository.findByTitleContainingIgnoreCase(titleFilter, pageable);

        return page.map(this::mapToResponse); // Используем тот же метод маппинга
    }

    @Override
    @Transactional
    public TopicResponseTo update(Long id, TopicRequestTo request) {
        validate(request);
        Topic existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic not found", "40412"));

        // Аналогично меняем код на 403 для тестов [cite: 1595, 2628]
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found", "40314"));

        existing.setUser(user);
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setModified(OffsetDateTime.now());

        Topic updated = repository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
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