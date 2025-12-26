package com.restApp.repository;

import com.restApp.model.Author;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorRepository extends AbstractInMemoryRepository<Author> {
    public Optional<Author> findByLogin(String login) {
        return storage.values().stream()
                .filter(author -> author.getLogin().equals(login))
                .findFirst();
    }
}
