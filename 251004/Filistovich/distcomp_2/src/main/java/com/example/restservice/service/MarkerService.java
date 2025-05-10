package com.example.restservice.service;

import com.example.restservice.model.Marker;
import com.example.restservice.repository.MarkerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkerService {
    private final MarkerRepository repository;

    @Autowired
    public MarkerService(MarkerRepository repository) {
        this.repository = repository;
    }

    public List<Marker> findAll() {
        return repository.findAll();
    }

    public Marker findById(Long id) {
        return repository.getMarkerById(id);
    }

    public Marker save(Marker marker) {
        return repository.save(marker);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteMarkerById(id);
    }

    public Marker update(Marker marker) {
        return repository.save(marker);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
