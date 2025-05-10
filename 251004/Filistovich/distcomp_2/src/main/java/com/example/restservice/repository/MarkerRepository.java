package com.example.restservice.repository;

import com.example.restservice.model.Marker;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    //List<Marker> getAll();
    Marker getMarkerById(@Positive Long id);
    void deleteMarkerById(@Positive Long id);
}
