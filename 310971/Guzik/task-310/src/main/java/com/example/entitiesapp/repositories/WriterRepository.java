package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Writer;

import java.util.Optional;

public interface WriterRepository extends CrudRepository<Writer, Long> {
    Optional<Writer> findByLogin(String login);
}