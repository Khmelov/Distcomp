package com.example.lab1.repository;

import java.util.List;

import com.example.lab1.model.Marker;

public interface MarkerRepository extends CrudRepository<Marker> {
    @Override
    List<Marker> getAllEntities();
}
