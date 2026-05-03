package com.example.news.repository;

import com.example.news.entity.Marker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarkerRepository extends JpaRepository<Marker, Long> {
    Page<Marker> findAll(Pageable pageable);
    boolean existsByName(String name);
}