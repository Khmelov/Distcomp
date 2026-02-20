package com.example.demo.repository;

import com.example.demo.model.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository extends InMemoryRepository<Author, Long>{
    @Override
    protected Long generatedId() {
        return id++;
    }

    @Override
    protected Long getId(Author author) {
        return author.getId();
    }

    @Override
    protected void setId(Author entity, Long id) {
        entity.setId(id);
    }
}
