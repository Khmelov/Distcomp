package com.lizaveta.notebook.repository;

import com.lizaveta.notebook.model.entity.Marker;

import java.util.Optional;

public interface MarkerRepository extends CrudRepository<Marker, Long> {

    Optional<Marker> findByName(String name);
}
