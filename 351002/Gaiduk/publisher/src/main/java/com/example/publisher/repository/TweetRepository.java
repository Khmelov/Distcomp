package com.example.publisher.repository;

import com.example.publisher.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    boolean existsByUserIdAndTitle(Long userId, String title);
}

