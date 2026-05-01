package com.example.demo.specification;

import com.example.demo.model.Issue;
import org.springframework.data.jpa.domain.Specification;

public class IssueSpecifications {
    public static Specification<Issue> titleLike(String title) {
        return (root, query, cb) -> title == null ? null :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Issue> contentLike(String content) {
        return (root, query, cb) -> content == null ? null :
                cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Issue> authorIdEquals(Long authorId) {
        return (root, query, cb) -> authorId == null ? null :
                cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Issue> markNameLike(String markName) {
        return (root, query, cb) -> {
            if (markName == null) return null;
            query.distinct(true);
            return cb.like(cb.lower(root.join("marks").get("name")), "%" + markName.toLowerCase() + "%");
        };
    }

    public static Specification<Issue> withFilters(String title, String content, Long authorId, String markName) {
        return Specification
                .where(titleLike(title))
                .and(contentLike(content))
                .and(authorIdEquals(authorId))
                .and(markNameLike(markName));
    }
}
