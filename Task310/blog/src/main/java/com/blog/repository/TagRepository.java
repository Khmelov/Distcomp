package com.blog.repository;

import com.blog.model.Tag;
import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}