package com.example.lab.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.publisher.model.Marker;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
}
