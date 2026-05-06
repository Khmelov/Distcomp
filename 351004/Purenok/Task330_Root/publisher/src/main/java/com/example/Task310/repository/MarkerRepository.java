package com.example.Task310.repository;
import com.example.Task310.bean.Marker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
    boolean existsByName(String name);
    Optional<Marker> findByName(String name); // Нужно для поиска существующих маркеров
}

