package com.markerservice.repositories;

import com.markerservice.dtos.MarkerResponseTo;
import com.markerservice.models.Marker;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    Optional<Marker> findMarkerById(Long id);

    Optional<Marker> findMarkerByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Marker> findAllByIdIn(List<Long> ids);

}
