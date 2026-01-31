package org.polozkov.repository.comment;

import org.polozkov.entity.comment.Comment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CommentRepository {

    private final ConcurrentHashMap<Long, Comment> comments = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Comment> findAll() {
        return new ArrayList<>(comments.values());
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(comments.get(id));
    }

    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(idCounter.getAndIncrement());
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    public Comment update(Comment comment) {
        if (!comments.containsKey(comment.getId())) {
            throw new RuntimeException("Comment not found with id: " + comment.getId());
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    public void deleteById(Long id) {
        comments.remove(id);
    }

    public boolean existsById(Long id) {
        return comments.containsKey(id);
    }

    public List<Comment> findByIssueId(Long issueId) {
        return comments.values().stream()
                .filter(comment -> comment.getIssue().getId().equals(issueId))
                .toList();
    }
}
