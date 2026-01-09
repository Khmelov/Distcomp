package org.example.task330.publisher.repository;

import org.example.task330.publisher.model.TweetLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetLabelRepository extends JpaRepository<TweetLabel, Long> {
    List<TweetLabel> findByTweetId(Long tweetId);
    
    List<TweetLabel> findByLabelId(Long labelId);
    
    Optional<TweetLabel> findByTweetIdAndLabelId(Long tweetId, Long labelId);
    
    void deleteByTweetId(Long tweetId);
    
    void deleteByLabelId(Long labelId);
    
    void deleteByTweetIdAndLabelId(Long tweetId, Long labelId);
}

