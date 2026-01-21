package com.example.discussion.repository;

import com.example.discussion.entity.Reaction;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, String> {
    List<Reaction> findByStoryId(Long storyId);
}