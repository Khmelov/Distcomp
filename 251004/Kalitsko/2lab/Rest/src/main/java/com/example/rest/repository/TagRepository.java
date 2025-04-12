package com.example.rest.repository;

import com.example.rest.entity.Creator;
import com.example.rest.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
