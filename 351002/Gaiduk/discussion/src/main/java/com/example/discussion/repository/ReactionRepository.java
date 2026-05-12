package com.example.discussion.repository;

import com.example.discussion.entity.Reaction;
import com.example.discussion.entity.ReactionKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, ReactionKey> {
}