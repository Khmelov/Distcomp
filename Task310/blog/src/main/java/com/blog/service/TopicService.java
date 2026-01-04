package com.blog.service;

import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import java.util.List;

public interface TopicService {
    List<TopicResponseTo> getAll();
    TopicResponseTo getById(Long id);
    TopicResponseTo create(TopicRequestTo request);
    TopicResponseTo update(Long id, TopicRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<TopicResponseTo> getByEditorId(Long editorId);
    List<TopicResponseTo> getByTagId(Long tagId);
}