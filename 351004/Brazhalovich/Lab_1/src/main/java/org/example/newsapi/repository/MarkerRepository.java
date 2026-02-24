package org.example.newsapi.repository;

import org.example.newsapi.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    boolean existsByName(String name); // Добавьте этот метод
}