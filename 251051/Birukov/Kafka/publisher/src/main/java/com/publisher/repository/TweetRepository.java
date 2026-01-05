package com.publisher.repository;

import com.publisher.entity.Tweet;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
	boolean existsById(Long id);
	Optional<Tweet> findById(Long id);
	List<Tweet> findAll();
	
	boolean existsByTitle(String title);
	Optional<Tweet> findByTitle(String title);
	List<Tweet> findByWriterId(Long writerId);
}