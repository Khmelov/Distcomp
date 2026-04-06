package com.example.forum.service;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.response.TopicResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicService {
    TopicResponseTo create(TopicRequestTo request);

    TopicResponseTo getById(Long id);

    Page<TopicResponseTo> getAll(String titleFilter, Pageable pageable);

    TopicResponseTo update(Long id, TopicRequestTo request);

    void delete(Long id);
}