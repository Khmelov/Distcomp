package com.example.restApi.repository;

import com.example.restApi.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends InMemoryRepository<Comment> {

    public List<Comment> findByIssueId(Long issueId) {
        return storage.values().stream()
                .filter(c -> c.getIssueId().equals(issueId))
                .toList();
    }
}
