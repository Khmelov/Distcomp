package com.example.rest.service;

import com.example.rest.dto.*;

import java.util.List;
import java.util.Optional;

public interface TagService {
    TagResponseTo create(TagRequestTo tag);

    TagResponseTo update(TagUpdate updatedTag);

    void deleteById(Long id);

    List<TagResponseTo> findAll();

    Optional<TagResponseTo> findById(Long id);
}