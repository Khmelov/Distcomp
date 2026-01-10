package com.blog.repository;

import com.blog.entity.Tag;

import java.util.Optional;

public interface TagRepository extends GenericRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}