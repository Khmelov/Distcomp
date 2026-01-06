package com.labs.domain.repository;

import com.labs.domain.entity.Label;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends BaseRepository<Label, Long> {
    Optional<Label> findByName(String name);
    List<Label> findByNameIn(List<String> names);
}

