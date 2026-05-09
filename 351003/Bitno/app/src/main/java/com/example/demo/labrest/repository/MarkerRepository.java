package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.Marker;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkerRepository extends BaseRepository<Marker, Long> {
    Optional<Marker> findByName(String name);
}