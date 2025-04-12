package org.example.discussion.repository;

import org.example.discussion.entity.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface MessageRepo extends CassandraRepository<Message, String> {
    default Stream<Message> getAll(){
        return findAll().stream();
    }
    Optional<Message> findByCountryAndStoryIdAndId(String country, long storyId, long id);
    Optional<Message> findByCountryAndId(String country, long id);

    default Optional<Message> create(Message input){
        return Optional.of(save(input));
    }
    default Optional<Message> update(Message input){
        return Optional.of(save(input));
    }
    boolean deleteByCountryAndId(String country, long id);
}
