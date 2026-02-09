package com.example.lab1.repository;

import java.util.List;

import com.example.lab1.model.Post;

public interface PostRepository extends CrudRepository<Post> {
    @Override
    List<Post> getAllEntities();
}
