package com.example.rest.repository;

import com.example.rest.entity.Story;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface StoryRepo extends Repository<Story> {
    @EntityGraph(attributePaths = {"labels"})
    Optional<Story> findById(long i);
    Story findByMessagesContaining(Long messageId);
}
