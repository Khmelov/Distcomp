package com.example.kafkademo.service;

import com.example.kafkademo.entity.Marker;
import com.example.kafkademo.repository.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MarkerService {

    @Autowired
    private MarkerRepository markerRepository;

    public List<Marker> findAll() {
        return markerRepository.findAll();
    }

    public Optional<Marker> findById(Long id) {
        return markerRepository.findById(id);
    }

    public Optional<Marker> findByName(String name) {
        return markerRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return markerRepository.existsByName(name);
    }

    public Marker save(Marker marker) {
        return markerRepository.save(marker);
    }

    public void deleteById(Long id) {
        markerRepository.deleteById(id);
    }
}