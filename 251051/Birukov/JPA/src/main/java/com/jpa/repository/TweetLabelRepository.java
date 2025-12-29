package com.jpa.repository;

import com.jpa.entity.TweetLabel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface TweetLabelRepository extends JpaRepository<TweetLabel, Long> {
	
	boolean existsById(Long id);
	Optional<TweetLabel> findById(Long id);
	List<TweetLabel> findAll();
	
	List<TweetLabel> findByTweetId(Long tweetId);
	List<TweetLabel> findByLabelId(Long labelId);
}