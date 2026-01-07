package com.task310.blogplatform.repository;

import com.task310.blogplatform.model.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);
    
    Page<Label> findAll(Pageable pageable);
    
    @Query("SELECT l FROM Label l WHERE l.name LIKE %:name%")
    Page<Label> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Article a JOIN a.labels l WHERE l.id = :labelId")
    long countArticlesByLabelId(@Param("labelId") Long labelId);
}

