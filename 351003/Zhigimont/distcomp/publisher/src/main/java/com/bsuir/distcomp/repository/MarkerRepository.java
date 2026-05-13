package com.bsuir.distcomp.repository;

import com.bsuir.distcomp.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    Optional<Marker> findByName(String name);

    @Modifying
    @Query(value = """
        DELETE FROM tbl_marker m
        WHERE NOT EXISTS (
            SELECT 1 FROM tbl_topic_marker tm
            WHERE tm.marker_id = m.id
        )
    """, nativeQuery = true)
    void deleteUnusedMarkers();
}
