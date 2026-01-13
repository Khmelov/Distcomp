package com.publick.repository;

import com.publick.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository extends InMemoryCrudRepository<Author, Long> {

    @Override
    protected Long getId(Author entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Author entity, Long id) {
        entity.setId(id);
    }
}