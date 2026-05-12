package com.example.task350.repository;

import com.example.task350.domain.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // ДОБАВИТЬ ЭТОТ ИМПОРТ

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    Optional<Marker> findByName(String name);
}