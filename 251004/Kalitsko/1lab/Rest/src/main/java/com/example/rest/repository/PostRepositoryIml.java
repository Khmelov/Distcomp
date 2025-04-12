package com.example.rest.repository;

import com.example.rest.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PostRepositoryIml implements PostRepository {

    private final Map<Long, Post> posts = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Post create(Post post) {
        post.setId(nextId++);
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public Post update(Post updatedPost) {
        if (!posts.containsKey(updatedPost.getId())) {
            throw new IllegalArgumentException("Post with ID " + updatedPost.getId() + " not found");
        }
        posts.put(updatedPost.getId(), updatedPost);
        return updatedPost;
    }

    @Override
    public void deleteById(Long id) {
        posts.remove(id);

    }

    @Override
    public List<Post> findAll() {
        return posts.values().stream().toList();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }
}
