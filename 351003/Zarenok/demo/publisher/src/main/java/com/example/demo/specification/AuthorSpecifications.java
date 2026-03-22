package com.example.demo.specification;

import com.example.demo.model.Author;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AuthorSpecifications {
    public static Specification<Author> loginLike(String login) {
        if (!StringUtils.hasText(login)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("login")), "%" + login.toLowerCase() + "%");
    }

    public static Specification<Author> firstnameLike(String firstname) {
        if (!StringUtils.hasText(firstname)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    public static Specification<Author> lastnameLike(String lastname) {
        if (!StringUtils.hasText(lastname)) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }

    public static Specification<Author> withFilters(String login, String firstname, String lastname) {
        return Specification
                .where(loginLike(login))
                .and(firstnameLike(firstname))
                .and(lastnameLike(lastname));
    }
}
