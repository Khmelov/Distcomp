package com.example.rest.repository;

import com.example.rest.entity.Label;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface LabelRepo extends Repository<Label> {
    List<Label> findByNameIn(List<String> name);
    @EntityGraph(attributePaths = {"stories"})
    Optional<Label> findById(long id);
}
