package com.bsuir.dc.repository;

import com.bsuir.dc.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    boolean existsByName(String name);
    List<Label> findByNameIn(Set<String> names);
}
