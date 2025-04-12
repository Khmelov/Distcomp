package com.example.rest.repository;

import com.example.rest.entity.Creator;
import com.example.rest.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    Tag create(Tag tag);
    Tag update(Tag updatedTag);
    void deleteById(Long id);
    List<Tag> findAll();
    Optional<Tag> findById(Long id);
}
