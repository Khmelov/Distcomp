package com.example.entitiesapp.repositories.impl;

import com.example.entitiesapp.entities.Post;
import com.example.entitiesapp.repositories.PostRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPostRepository implements PostRepository {
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(idCounter.getAndIncrement());
            post.setCreated(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        post.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    @Override
    public Post update(Post post) {
        if (!posts.containsKey(post.getId())) {
            throw new IllegalArgumentException("Post not found with id: " + post.getId());
        }
        post.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public boolean deleteById(Long id) {
        return posts.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return posts.containsKey(id);
    }

    @Override
    public List<Post> findByArticleId(Long articleId) {
        return posts.values().stream()
                .filter(post -> post.getArticleId() != null && post.getArticleId().equals(articleId))
                .collect(Collectors.toList());
    }
}