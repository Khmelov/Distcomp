package com.example.storyapp.repository;

import com.example.storyapp.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryCommentRepository implements CrudRepository<Comment, Long> {
    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Comment> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(idGenerator.getAndIncrement());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public List<Comment> findByStoryId(Long storyId) {
        return store.values().stream()
                .filter(comment -> storyId.equals(comment.getStoryId()))
                .collect(Collectors.toList());
    }
}