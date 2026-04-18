package com.example.forum.service;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;

import java.util.List;

public interface PostService {

    PostResponseTo create(PostRequestTo request);

    PostResponseTo getById(Long id);

    List<PostResponseTo> getAll();

    PostResponseTo update(Long id, PostRequestTo request);

    void delete(Long id);
}
