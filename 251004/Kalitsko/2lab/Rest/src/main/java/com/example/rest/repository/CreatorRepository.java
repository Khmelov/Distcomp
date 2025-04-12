package com.example.rest.repository;

import com.example.rest.entity.Creator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreatorRepository extends CrudRepository<Creator, Long> {
}
