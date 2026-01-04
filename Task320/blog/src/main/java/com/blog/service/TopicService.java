package com.blog.service;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TopicService {
    List<TopicResponseTo> getAll();
    Page<TopicResponseTo> getAll(Pageable pageable);
    TopicResponseTo getById(Long id);
    TopicResponseTo create(TopicRequestTo request);
    TopicResponseTo update(Long id, TopicRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<TopicResponseTo> getByEditorId(Long editorId);
    List<TopicResponseTo> getByTagId(Long tagId);

    // Методы с пагинацией
    Page<TopicResponseTo> getByEditorId(Long editorId, Pageable pageable);
    Page<TopicResponseTo> getByTagId(Long tagId, Pageable pageable);
}