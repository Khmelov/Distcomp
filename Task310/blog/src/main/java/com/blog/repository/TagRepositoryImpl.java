package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TagRepositoryImpl extends InMemoryGenericRepository<Tag> implements TagRepository {

    @Override
    public Optional<Tag> findByName(String name) {
        return storage.values().stream()
                .filter(tag -> name.equals(tag.getName()))
                .findFirst();
    }
}