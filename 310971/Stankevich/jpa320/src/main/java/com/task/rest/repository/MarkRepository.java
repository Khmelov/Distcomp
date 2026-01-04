package com.task.rest.repository;

import com.task.rest.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long>, JpaSpecificationExecutor<Mark> {
    Optional<Mark> findByName(String name);
}