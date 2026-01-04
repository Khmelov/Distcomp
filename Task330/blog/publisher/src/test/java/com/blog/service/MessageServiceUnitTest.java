package com.blog.service;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Topic;
import com.blog.repository.TopicRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceUnitTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Topic topic;
    private Message message;
    private MessageRequestTo messageRequest;
    private MessageResponseTo messageResponse;

    @BeforeEach
    void setUp() {
        topic = new Topic();
        topic.setId(1L);
        topic.setTitle("Тестовая тема");

        message = new Message();
        message.setId(1L);
        message.setTopic(topic);
        message.setContent("Тестовое сообщение");

        messageRequest = new MessageRequestTo();
        messageRequest.setTopicId(1L);
        messageRequest.setContent("Тестовое сообщение");

        messageResponse = new MessageResponseTo();
        messageResponse.setId(1L);
        messageResponse.setTopicId(1L);
        messageResponse.setContent("Тестовое сообщение");
    }

    @Test
    void shouldGetAllMessages() {
        // Given
        List<Message> messages = Arrays.asList(message);
        when(messageRepository.findAll()).thenReturn(messages);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        List<MessageResponseTo> result = messageService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Тестовое сообщение", result.get(0).getContent());
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllMessagesWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message));
        when(messageRepository.findAll(pageable)).thenReturn(messagePage);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        Page<MessageResponseTo> result = messageService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(messageRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetMessageById() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        MessageResponseTo result = messageService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовое сообщение", result.getContent());
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMessageNotFound() {
        // Given
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> messageService.getById(999L));
        verify(messageRepository, times(1)).findById(999L);
    }

    @Test
    void shouldCreateMessage() {
        // Given
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(messageMapper.toEntity(messageRequest)).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        MessageResponseTo result = messageService.create(messageRequest);

        // Then
        assertNotNull(result);
        assertEquals("Тестовое сообщение", result.getContent());
        verify(topicRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingMessageWithNonExistentTopic() {
        // Given
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> messageService.create(messageRequest));
        verify(topicRepository, times(1)).findById(999L);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void shouldUpdateMessage() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        MessageResponseTo result = messageService.update(1L, messageRequest);

        // Then
        assertNotNull(result);
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void shouldDeleteMessage() {
        // Given
        when(messageRepository.existsById(1L)).thenReturn(true);

        // When
        messageService.delete(1L);

        // Then
        verify(messageRepository, times(1)).existsById(1L);
        verify(messageRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldGetMessagesByTopicId() {
        // Given
        List<Message> messages = Arrays.asList(message);
        when(messageRepository.findByTopicId(1L)).thenReturn(messages);
        when(messageMapper.toResponse(message)).thenReturn(messageResponse);

        // When
        List<MessageResponseTo> result = messageService.getByTopicId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTopicId());
        verify(messageRepository, times(1)).findByTopicId(1L);
    }
}