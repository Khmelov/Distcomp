package com.tweetservice.repositories;

import com.tweetservice.models.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findTweetByTitle(String title);

    Optional<Tweet> findTweetById(Long id);

    boolean existsByTitleAndIdNot(String title, Long id);

    List<Tweet> findAllByWriterId(Long writerId);
}
