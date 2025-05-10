package com.example.restservice.repository;

import com.example.restservice.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Long> {
    public Message findMessageByCountryAndId(String country, Long id);
    public void deleteByCountryAndId(String country, Long id);
}
