package org.example.task330.publisher.repository;

import org.example.task330.publisher.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByWriterId(Long writerId);
    
    Optional<Tweet> findByTitle(String title);
}

