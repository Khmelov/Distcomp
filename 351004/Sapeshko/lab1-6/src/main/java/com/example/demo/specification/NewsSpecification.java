package com.example.demo.specification;

import com.example.demo.entity.News;
import org.springframework.data.jpa.domain.Specification;

public class NewsSpecification {
    public static Specification<News> titleContains(String title) {
        return (root, query, cb) -> title == null ? cb.conjunction() : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<News> contentContains(String content) {
        return (root, query, cb) -> content == null ? cb.conjunction() : cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<News> authorIdEquals(Long authorId) {
        return (root, query, cb) -> authorId == null ? cb.conjunction() : cb.equal(root.get("author").get("id"), authorId);
    }
}