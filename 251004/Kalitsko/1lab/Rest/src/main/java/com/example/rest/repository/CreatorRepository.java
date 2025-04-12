package com.example.rest.repository;

import com.example.rest.entity.Creator;

import java.util.List;
import java.util.Optional;

public interface CreatorRepository {

    Creator create(Creator creator);
    Creator update(Creator updatedCreator);
    void deleteById(Long id);
    List<Creator> findAll();
    Optional<Creator> findById(Long id);
}
