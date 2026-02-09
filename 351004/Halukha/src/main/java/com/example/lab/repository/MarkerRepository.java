package com.example.lab.repository;

import java.util.List;

import com.example.lab.model.Marker;

public interface MarkerRepository extends CrudRepository<Marker> {
    @Override
    List<Marker> getAllEntities();
}
