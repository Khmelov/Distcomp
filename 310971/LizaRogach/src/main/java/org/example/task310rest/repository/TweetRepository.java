package org.example.task310rest.repository;

import org.example.task310rest.model.Tweet;
import org.springframework.stereotype.Repository;

@Repository
public class TweetRepository extends InMemoryCrudRepository<Tweet> {
    public TweetRepository() {
        super(Tweet::getId, Tweet::setId);
    }
}


