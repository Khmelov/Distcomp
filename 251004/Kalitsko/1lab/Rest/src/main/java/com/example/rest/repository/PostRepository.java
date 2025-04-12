package com.example.rest.repository;

import com.example.rest.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post create(Post post);
    Post update(Post updatedPost);
    void deleteById(Long id);
    List<Post> findAll();
    Optional<Post> findById(Long id);
}
