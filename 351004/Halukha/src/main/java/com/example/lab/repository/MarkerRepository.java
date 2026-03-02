package com.example.lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.model.Marker;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
}
