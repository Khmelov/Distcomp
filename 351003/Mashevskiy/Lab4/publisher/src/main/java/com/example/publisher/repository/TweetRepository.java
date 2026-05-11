package com.example.publisher.repository;

import com.example.publisher.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByCreatorId(Long creatorId);
}