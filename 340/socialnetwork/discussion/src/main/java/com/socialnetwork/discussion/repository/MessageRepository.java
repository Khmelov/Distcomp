package com.socialnetwork.discussion.repository;

import com.socialnetwork.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CassandraRepository<Message, Long> {

    // Поиск сообщений по tweetId и country
    @Query("SELECT * FROM tbl_message WHERE country = ?0 AND tweet_id = ?1")
    List<Message> findByCountryAndTweetId(String country, Long tweetId);

    // Поиск всех сообщений для твита (используем ALLOW FILTERING для демонстрации)
    @Query("SELECT * FROM tbl_message WHERE tweet_id = ?0 ALLOW FILTERING")
    List<Message> findByTweetId(Long tweetId);

    // Поиск сообщений по стране
    List<Message> findByCountry(String country);

    // Поиск по ID (неэффективно в Cassandra, но для тестов)
    @Query("SELECT * FROM tbl_message WHERE id = ?0 ALLOW FILTERING")
    Optional<Message> findById(Long id);
}