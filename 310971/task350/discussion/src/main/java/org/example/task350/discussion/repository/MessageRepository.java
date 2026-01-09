package org.example.task350.discussion.repository;

import org.example.task350.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Message.MessageKey> {
    
    @Query("SELECT * FROM distcomp.tbl_message WHERE country = ?0 AND tweet_id = ?1")
    List<Message> findAllByCountryAndTweetId(String country, Long tweetId);
}

