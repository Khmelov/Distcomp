package com.example.demo.specification;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {
    public static Specification<Comment> contentContains(String content) {
        return (root, query, cb) -> content == null ? cb.conjunction() : cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Comment> newsIdEquals(Long newsId) {
        return (root, query, cb) -> newsId == null ? cb.conjunction() : cb.equal(root.get("news").get("id"), newsId);
    }
}