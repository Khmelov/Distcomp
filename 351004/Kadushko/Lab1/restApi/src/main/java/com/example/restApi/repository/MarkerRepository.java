package com.example.restApi.repository;

import com.example.restApi.model.Marker;
import org.springframework.stereotype.Repository;

@Repository
public class MarkerRepository extends InMemoryRepository<Marker> {

    public boolean existsByName(String name) {
        return storage.values().stream()
                .anyMatch(m -> m.getName().equals(name));
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return storage.values().stream()
                .anyMatch(m -> m.getName().equals(name) && !m.getId().equals(id));
    }
}
