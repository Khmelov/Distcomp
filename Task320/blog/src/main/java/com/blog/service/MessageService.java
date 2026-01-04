package com.blog.service;

import com.blog.dto.request.MessageRequestTo;
import com.blog.dto.response.MessageResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    List<MessageResponseTo> getAll();
    Page<MessageResponseTo> getAll(Pageable pageable);
    MessageResponseTo getById(Long id);
    MessageResponseTo create(MessageRequestTo request);
    MessageResponseTo update(Long id, MessageRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<MessageResponseTo> getByTopicId(Long topicId);
    Page<MessageResponseTo> getByTopicId(Long topicId, Pageable pageable);
}