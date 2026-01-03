package com.task.rest.repository;

import com.task.rest.model.Author;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorRepository extends InMemoryRepository<Author> {

    @Override
    protected Long getId(Author entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Author entity, Long id) {
        entity.setId(id);
    }

    public Optional<Author> findByLogin(String login) {
        return storage.values().stream()
                .filter(author -> author.getLogin().equals(login))
                .findFirst();
    }
}