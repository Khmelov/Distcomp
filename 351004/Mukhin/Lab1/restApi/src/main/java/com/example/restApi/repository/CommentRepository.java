package com.example.restApi.repository;

import com.example.restApi.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommentRepository extends InMemoryRepository<Comment> {

    public List<Comment> findByArticleId(Long articleId) {
        return storage.values().stream()
                .filter(comment -> comment.getArticleId().equals(articleId))
                .collect(Collectors.toList());
    }
}
