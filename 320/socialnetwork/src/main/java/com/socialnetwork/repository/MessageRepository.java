package com.socialnetwork.repository;

import com.socialnetwork.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAll(Pageable pageable);

    List<Message> findByTweetId(Long tweetId);

    @Query("SELECT m FROM Message m WHERE m.tweet.id = :tweetId")
    Page<Message> findByTweetId(@Param("tweetId") Long tweetId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.id = :id")
    boolean existsById(@Param("id") Long id);

    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.tweet.id = :tweetId")
    boolean existsByTweetId(@Param("tweetId") Long tweetId);
}