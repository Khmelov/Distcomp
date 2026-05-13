package org.rv.lab1.repo;

import org.rv.lab1.domain.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    Optional<Marker> findByName(String name);
    boolean existsByName(String name);

    @org.springframework.data.jpa.repository.Query("""
            select count(s)
            from Story s
            join s.markers m
            where m.id = :markerId
            """)
    long countStoriesUsingMarker(@org.springframework.data.repository.query.Param("markerId") Long markerId);
}

