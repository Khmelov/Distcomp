package com.example.forum.service;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponseTo create(PostRequestTo request);

    PostResponseTo getById(Long topicId, Long id);

    Page<PostResponseTo> getAll(Long topicId, Pageable pageable);

    PostResponseTo update(Long id, PostRequestTo request);

    void delete(Long id);
}

