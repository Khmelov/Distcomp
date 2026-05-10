package com.example.task310.repository;

import com.example.task310.domain.entity.Tweet;
import org.springframework.stereotype.Repository;

@Repository
public class TweetRepository extends InMemoryRepository<Tweet> {
}