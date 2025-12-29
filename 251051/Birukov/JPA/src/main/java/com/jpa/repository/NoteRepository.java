package com.jpa.repository;

import com.jpa.entity.Note;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	
	boolean existsById(Long id);
	Optional<Note> findById(Long id);
	List<Note> findAll();
	
	List<Note> findByTweetId(Long tweetId);
}