package com.blog.service.impl;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.exception.DuplicateResourceException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.TopicMapper;
import com.blog.model.Editor;
import com.blog.model.Topic;
import com.blog.repository.EditorRepository;
import com.blog.repository.TopicRepository;
import com.blog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private EditorRepository editorRepository;

    @Override
    public List<TopicResponseTo> getAll() {
        return topicRepository.findAll().stream()
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TopicResponseTo> getAll(Pageable pageable) {
        return topicRepository.findAll(pageable)
                .map(topicMapper::toResponse);
    }

    @Override
    public TopicResponseTo getById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        return topicMapper.toResponse(topic);
    }

    @Override
    public TopicResponseTo create(TopicRequestTo request) {
        // Проверяем существование редактора
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + request.getEditorId()));

        if (topicRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Topic with title '" + request.getTitle() + "' already exists");
        }

        Topic topic = topicMapper.toEntity(request);
        topic.setEditor(editor);

        Topic savedTopic = topicRepository.save(topic);
        return topicMapper.toResponse(savedTopic);
    }

    @Override
    public TopicResponseTo update(Long id, TopicRequestTo request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        // Проверяем существование редактора (если изменился)
        if (!topic.getEditor().getId().equals(request.getEditorId())) {
            Editor editor = editorRepository.findById(request.getEditorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Editor not found with id: " + request.getEditorId()));
            topic.setEditor(editor);
        }

        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());

        // Обновляем теги через маппер
        Topic updatedTopic = topicRepository.save(topic);
        return topicMapper.toResponse(updatedTopic);
    }

    @Override
    public void delete(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Topic not found with id: " + id);
        }
        topicRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return topicRepository.existsById(id);
    }

    @Override
    public List<TopicResponseTo> getByEditorId(Long editorId) {
        List<Topic> topics = topicRepository.findByEditorId(editorId);
        return topics.stream()
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopicResponseTo> getByTagId(Long tagId) {
        List<Topic> topics = topicRepository.findByTagId(tagId);
        return topics.stream()
                .map(topicMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TopicResponseTo> getByEditorId(Long editorId, Pageable pageable) {
        Page<Topic> topics = topicRepository.findByEditorId(editorId, pageable);
        return topics.map(topicMapper::toResponse);
    }

    @Override
    public Page<TopicResponseTo> getByTagId(Long tagId, Pageable pageable) {
        Page<Topic> topics = topicRepository.findByTagId(tagId, pageable);
        return topics.map(topicMapper::toResponse);
    }
}

