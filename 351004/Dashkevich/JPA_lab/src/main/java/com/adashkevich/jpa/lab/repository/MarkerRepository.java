package com.adashkevich.jpa.lab.repository;

import com.adashkevich.jpa.lab.model.Marker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    List<Marker> findByIdIn(Collection<Long> ids);

    Optional<Marker> findByNameIgnoreCase(String name);
}
