package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.Creator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatorRepository extends BaseRepository<Creator, Long> {
    boolean existsByLogin(String login);
    Optional<Creator> findByLogin(String login);
}