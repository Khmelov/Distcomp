package com.example.demo.specification;

import com.example.demo.entity.Author;
import org.springframework.data.jpa.domain.Specification;

public class AuthorSpecification {
    public static Specification<Author> loginEquals(String login) {
        return (root, query, cb) -> login == null ? cb.conjunction() : cb.equal(root.get("login"), login);
    }

    public static Specification<Author> firstnameContains(String firstname) {
        return (root, query, cb) -> firstname == null ? cb.conjunction() : cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    public static Specification<Author> lastnameContains(String lastname) {
        return (root, query, cb) -> lastname == null ? cb.conjunction() : cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }
}