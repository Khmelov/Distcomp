package com.example.rest.service;



import com.example.rest.dto.post.PostRequestTo;
import com.example.rest.dto.post.PostResponseTo;
import com.example.rest.dto.post.PostUpdate;

import java.util.List;
import java.util.Optional;

public interface PostService {
    PostResponseTo create(PostRequestTo post);

    PostResponseTo update(PostUpdate updatedPost);

    void deleteById(Long id);

    List<PostResponseTo> findAll();

    Optional<PostResponseTo> findById(Long id);
}
