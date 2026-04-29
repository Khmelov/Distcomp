package com.example.demo.specification;

import com.example.demo.model.Mark;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class MarkSpecifications {
    public static Specification<Mark> nameLike(String name){
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Mark> withFilters(String name) {
        return Specification.where(nameLike(name));
    }
}
