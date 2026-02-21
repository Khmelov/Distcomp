package com.lizaveta.notebook.repository.jpa;

import com.lizaveta.notebook.repository.entity.MarkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkerJpaRepository extends JpaRepository<MarkerEntity, Long> {

    Optional<MarkerEntity> findByName(String name);
}
