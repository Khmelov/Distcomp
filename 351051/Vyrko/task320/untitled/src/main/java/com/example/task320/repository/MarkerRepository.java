package com.example.task320.repository;

import com.example.task320.entity.Marker;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Репозиторий для выполнения операций с метками в базе данных.
@Repository
public interface MarkerRepository extends BaseRepository<Marker, Long> {

    // Поиск метки по названию.
    Optional<Marker> findByName(String name);
}