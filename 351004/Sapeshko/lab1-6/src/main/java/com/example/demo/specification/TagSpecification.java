package com.example.demo.specification;

import com.example.demo.entity.Tag;
import org.springframework.data.jpa.domain.Specification;

public class TagSpecification {
    public static Specification<Tag> nameContains(String name) {
        return (root, query, cb) -> name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}