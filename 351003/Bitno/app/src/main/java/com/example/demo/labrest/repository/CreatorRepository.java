package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.Creator;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends BaseRepository<Creator, Long> {
    boolean existsByLogin(String login);
}