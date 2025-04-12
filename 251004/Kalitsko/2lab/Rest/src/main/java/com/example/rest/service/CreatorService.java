package com.example.rest.service;

import com.example.rest.dto.creator.CreatorRequestTo;
import com.example.rest.dto.creator.CreatorResponseTo;
import com.example.rest.dto.creator.CreatorUpdate;

import java.util.List;
import java.util.Optional;

public interface CreatorService {
    CreatorResponseTo create(CreatorRequestTo creator);
    CreatorResponseTo update(CreatorUpdate updatedCreator);
    void deleteById(Long id);
    List<CreatorResponseTo> findAll();
    Optional<CreatorResponseTo> findById(Long id);

}
