package com.distcomp.publisher.repository;

import com.distcomp.publisher.model.TweetMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TweetMarkerRepository extends JpaRepository<TweetMarker, Long> {
    List<TweetMarker> findByTweetId(Long tweetId);
    void deleteByTweetId(Long tweetId);
}