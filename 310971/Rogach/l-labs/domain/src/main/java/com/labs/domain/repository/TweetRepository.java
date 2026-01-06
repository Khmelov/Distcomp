package com.labs.domain.repository;

import com.labs.domain.entity.Tweet;
import com.labs.domain.entity.Writer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends BaseRepository<Tweet, Long> {
    @Query("SELECT t.writer FROM Tweet t WHERE t.id = :tweetId")
    Writer findWriterByTweetId(@Param("tweetId") Long tweetId);

    @Query("SELECT t.labels FROM Tweet t WHERE t.id = :tweetId")
    List<com.labs.domain.entity.Label> findLabelsByTweetId(@Param("tweetId") Long tweetId);

    @Query("SELECT t.messages FROM Tweet t WHERE t.id = :tweetId")
    List<com.labs.domain.entity.Message> findMessagesByTweetId(@Param("tweetId") Long tweetId);
}

