package com.example.distcomp_1.service;

import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.Marker;
import com.example.distcomp_1.repository.AuthorRepository;
import com.example.distcomp_1.repository.MarkerRepository;
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
        return repository.getMarkers();
    }

    public Marker findById(Long id) {
        return repository.get(id);
    }

    public Marker save(Marker marker) {
        return repository.add(marker);
    }

    public void deleteById(Long id) {
        repository.delete(id);
    }

    public Marker update(Marker marker) {
        return repository.update(marker);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
