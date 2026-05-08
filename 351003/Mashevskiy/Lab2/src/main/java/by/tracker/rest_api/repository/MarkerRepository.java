package by.tracker.rest_api.repository;

import by.tracker.rest_api.entity.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    boolean existsByName(String name);
    Optional<Marker> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Marker m WHERE m.name = :name")
    void deleteByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_marker WHERE id NOT IN (SELECT DISTINCT marker_id FROM tbl_tweet_marker)", nativeQuery = true)
    void deleteOrphanMarkers();
}