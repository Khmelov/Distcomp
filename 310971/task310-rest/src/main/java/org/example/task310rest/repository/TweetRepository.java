package org.example.task310rest.repository;

import org.example.task310rest.model.Tweet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TweetRepository extends InMemoryCrudRepository<Tweet> {
    public TweetRepository() {
        super(Tweet::getId, Tweet::setId);
    }

    public List<Tweet> findByWriterId(Long writerId) {
        return findAll().stream()
                .filter(tweet -> writerId.equals(tweet.getWriterId()))
                .collect(Collectors.toList());
    }

    public Optional<Tweet> findByTitle(String title) {
        return findAll().stream()
                .filter(tweet -> title.equals(tweet.getTitle()))
                .findFirst();
    }
}
