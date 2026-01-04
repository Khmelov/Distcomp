package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Post;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByArticleId(Long articleId);
}