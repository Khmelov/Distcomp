package com.blog.service;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.TopicMapper;
import com.blog.model.Editor;
import com.blog.model.Tag;
import com.blog.model.Topic;
import com.blog.repository.EditorRepository;
import com.blog.repository.TopicRepository;
import com.blog.service.impl.TopicServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceUnitTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private EditorRepository editorRepository;

    @InjectMocks
    private TopicServiceImpl topicService;

    private Editor editor;
    private Topic topic;
    private TopicRequestTo topicRequest;
    private TopicResponseTo topicResponse;
    private Tag tag;

    @BeforeEach
    void setUp() {
        editor = new Editor();
        editor.setId(1L);
        editor.setLogin("editor@example.com");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("Java");

        topic = new Topic();
        topic.setId(1L);
        topic.setEditor(editor);
        topic.setTitle("Тестовая тема");
        topic.setContent("Содержание тестовой темы");
        topic.setTags(new HashSet<>(Arrays.asList(tag)));
        topic.setCreated(LocalDateTime.now());
        topic.setModified(LocalDateTime.now());

        topicRequest = new TopicRequestTo();
        topicRequest.setEditorId(1L);
        topicRequest.setTitle("Тестовая тема");
        topicRequest.setContent("Содержание тестовой темы");
        topicRequest.setTagIds(new HashSet<>(Arrays.asList(1L)));

        topicResponse = new TopicResponseTo();
        topicResponse.setId(1L);
        topicResponse.setEditorId(1L);
        topicResponse.setTitle("Тестовая тема");
        topicResponse.setContent("Содержание тестовой темы");
        topicResponse.setTagIds(new HashSet<>(Arrays.asList(1L)));
        topicResponse.setCreated(LocalDateTime.now());
        topicResponse.setModified(LocalDateTime.now());
    }

    @Test
    void shouldGetAllTopics() {
        // Given
        List<Topic> topics = Arrays.asList(topic);
        when(topicRepository.findAll()).thenReturn(topics);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        List<TopicResponseTo> result = topicService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Тестовая тема", result.get(0).getTitle());
        verify(topicRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllTopicsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(topic));
        when(topicRepository.findAll(pageable)).thenReturn(topicPage);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        Page<TopicResponseTo> result = topicService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(topicRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetTopicById() {
        // Given
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        TopicResponseTo result = topicService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовая тема", result.getTitle());
        verify(topicRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTopicNotFound() {
        // Given
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.getById(999L));
        verify(topicRepository, times(1)).findById(999L);
    }

    @Test
    void shouldCreateTopic() {
        // Given
        when(editorRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(topicMapper.toEntity(topicRequest)).thenReturn(topic);
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        TopicResponseTo result = topicService.create(topicRequest);

        // Then
        assertNotNull(result);
        assertEquals("Тестовая тема", result.getTitle());
        verify(editorRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTopicWithNonExistentEditor() {
        // Given
        when(editorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.create(topicRequest));
        verify(editorRepository, times(1)).findById(999L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void shouldUpdateTopic() {
        // Given
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(editorRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        TopicResponseTo result = topicService.update(1L, topicRequest);

        // Then
        assertNotNull(result);
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void shouldDeleteTopic() {
        // Given
        when(topicRepository.existsById(1L)).thenReturn(true);

        // When
        topicService.delete(1L);

        // Then
        verify(topicRepository, times(1)).existsById(1L);
        verify(topicRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldGetTopicsByEditorId() {
        // Given
        List<Topic> topics = Arrays.asList(topic);
        when(topicRepository.findByEditorId(1L)).thenReturn(topics);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        List<TopicResponseTo> result = topicService.getByEditorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEditorId());
        verify(topicRepository, times(1)).findByEditorId(1L);
    }

    @Test
    void shouldGetTopicsByTagId() {
        // Given
        List<Topic> topics = Arrays.asList(topic);
        when(topicRepository.findByTagId(1L)).thenReturn(topics);
        when(topicMapper.toResponse(topic)).thenReturn(topicResponse);

        // When
        List<TopicResponseTo> result = topicService.getByTagId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(topicRepository, times(1)).findByTagId(1L);
    }
}