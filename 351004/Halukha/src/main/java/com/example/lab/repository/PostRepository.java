package com.example.lab.repository;

import java.util.List;

import com.example.lab.model.Post;

public interface PostRepository extends CrudRepository<Post> {
    @Override
    List<Post> getAllEntities();
}
