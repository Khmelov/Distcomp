package com.example.discussion.repository;


import com.example.discussion.model.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByLogin(String login);
}
