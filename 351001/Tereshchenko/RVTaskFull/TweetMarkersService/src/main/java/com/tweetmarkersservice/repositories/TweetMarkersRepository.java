package com.tweetmarkersservice.repositories;

import com.tweetmarkersservice.models.TweetMarkerId;
import com.tweetmarkersservice.models.TweetMarkers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TweetMarkersRepository extends JpaRepository<TweetMarkers, TweetMarkerId> {

    boolean existsByTweetIdAndMarkerId(Long tweetId, Long markerId);

    @Query("SELECT tm.markerId FROM TweetMarkers tm WHERE tm.tweetId = :tweetId")
    List<Long> findMarkerIdsByTweetId(@Param("tweetId") Long tweetId);

    void deleteAllByTweetId(Long tweetId);

    @Query("SELECT DISTINCT tm.markerId FROM TweetMarkers tm WHERE tm.markerId IN :markerIds")
    List<Long> findUsedMarkerIds(@Param("markerIds") List<Long> markerIds);
}
