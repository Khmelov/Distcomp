package com.sergey.orsik.repository;

import com.sergey.orsik.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {
    Optional<Label> findByName(String name);
    List<Label> findAllByTweetsIsEmpty();
}
