package com.example.forum.service;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostResponseTo create(PostRequestTo request);
    PostResponseTo getById(Long id);
    List<PostResponseTo> getAll(Long topicId);
    PostResponseTo update(Long id, PostRequestTo request);
    void delete(Long id);
}