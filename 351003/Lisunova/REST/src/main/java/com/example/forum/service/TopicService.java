package com.example.forum.service;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.response.TopicResponseTo;

import java.util.List;

public interface TopicService {

    TopicResponseTo create(TopicRequestTo request);

    TopicResponseTo getById(Long id);

    List<TopicResponseTo> getAll();

    TopicResponseTo update(Long id, TopicRequestTo request);

    void delete(Long id);
}
