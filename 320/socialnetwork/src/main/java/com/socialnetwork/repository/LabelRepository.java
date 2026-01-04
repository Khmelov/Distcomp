package com.socialnetwork.repository;

import com.socialnetwork.model.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByName(String name);

    boolean existsByName(String name);

    Page<Label> findAll(Pageable pageable);

    @Query("SELECT COUNT(l) > 0 FROM Label l WHERE l.id = :id")
    boolean existsById(@Param("id") Long id);
}