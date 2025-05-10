package com.example.discussion.repository;

import com.example.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Long> {
    Optional<Message> findByCountryAndId(String country, long id);
    boolean existsByCountryAndId(String country, long id);

    boolean deleteByCountryAndId(String country, long id);

}