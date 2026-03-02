package com.example.restApi.repository;

import com.example.restApi.model.Marker;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MarkerRepository extends InMemoryRepository<Marker> {

    public Optional<Marker> findByName(String name) {
        return storage.values().stream()
                .filter(marker -> marker.getName().equals(name))
                .findFirst();
    }
}
