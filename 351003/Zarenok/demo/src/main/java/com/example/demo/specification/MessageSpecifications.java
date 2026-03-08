package com.example.demo.specification;

import com.example.demo.model.Issue;
import com.example.demo.model.Message;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class MessageSpecifications {
    public static Specification<Message> contentLike(String content){
        if (!StringUtils.hasText(content)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Message> issueIdEquals(Long issueId) {
        return (root, query, cb) -> issueId == null ? null :
                cb.equal(root.get("issue").get("id"), issueId);
    }

    public static Specification<Message> withFilters(String content, Long issueId) {
        return Specification
                .where(contentLike(content))
                .and(issueIdEquals(issueId));
    }

}
